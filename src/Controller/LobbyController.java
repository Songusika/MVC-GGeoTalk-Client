/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.UserAccount;
import Model.FriendList;
import Model.RoomList;
import Model.FriendInfo;
import Model.LobbyModel;
import Model.RoomInfo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import View.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Seok17
 */
public class LobbyController extends Thread {

    UserAccount user;
    FriendList friendlist;
    FriendInfo friendInfo;
    RoomList roomlist;
    RoomInfo roominfo;
    LobbyModel lobbymodel;

    ServerInfo serverInfo = ServerInfo.getInstance();
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    LobbyView lobby;
    int now = 0; //0 이면 친구창, 1이면 방리스트창  
    static int GETROOM = 1, GETFRIEND = 0;

    public LobbyController(UserAccount user) {
        this.user = user;
        lobby = new LobbyView();
        friendlist = new FriendList(user);
        friendInfo = new FriendInfo(user.getId());
        roomlist = new RoomList();
        roominfo = new RoomInfo();
        lobbymodel = new LobbyModel();

        lobby.plusBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lobby.plusBtn.equals(e.getSource())) {
                    addBtnClicked();
                }
            }
        });

        lobby.refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lobby.refreshBtn.equals(e.getSource())) {
                    RefreshBtnClicked();
                }
            }
        });
        lobby.showFriendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lobby.showFriendBtn.equals(e.getSource())) {
                    FriendBtnClicked();
                }
            }
        });
        lobby.showRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lobby.showRoomBtn.equals(e.getSource())) {
                    RoomBtnClicked();
                }
            }
        });

    }

    public void mouseClicked(MouseEvent evt) {
        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
            new ChattingRoomView();
        }
    }

    public void RoomBtnClicked() { //JList 업데이트 해야함. --> 방목록으로 
        now = 0;
        lobby.mode.setText("Chatting");
        roomlist = getRoomlist();
        remakelist(refresh(roomlist.getRoomNames(), roomlist.getlen()));

    }

    public void FriendBtnClicked() { //JList 업데이트 해야함. --> 친구목록으로 
        now = 1;
        lobby.mode.setText("Friend");
        friendlist = getFriendlist();
        remakelist(refresh(friendlist.getNames(), friendlist.getlen()));

    }

    public void RefreshBtnClicked() {
        switch (now) {
            case 0:
                roomlist = getRoomlist();
                remakelist(refresh(roomlist.getRoomNames(), roomlist.getlen()));
                break;
            case 1:
                friendlist = getFriendlist();
                remakelist(refresh(friendlist.getNames(), friendlist.getlen()));
                break;
        }
    }

    public void addBtnClicked() {
        switch (now) {
            case 0:
                addRoom();
                break;
            case 1:
                addFriend();
                break;
        }
    }

    public DefaultListModel<String> refresh(String[] renames, int len) {
        DefaultListModel<String> names = new DefaultListModel<String>();
        switch (now) {
            case 0:
                for (int i = 0; i < len; i++) {
                    names.addElement(" " + (i+1) + " 번방   " + renames[i]);
                }
                return names;
            case 1:
                for (int i = 0; i < len; i++) {
                    System.out.println(renames[i]);
                    names.addElement(renames[i]);
                }
                return names;
        }
        return null;
    }

    public void remakelist(DefaultListModel<String> roomNames) {
        lobby.Jlist = new JList(roomNames);
        lobby.Jlist.setBackground(new java.awt.Color(64, 68, 75));
        lobby.Jlist.setFont(new java.awt.Font("휴먼엑스포", 0, 16)); // NOI18N
        lobby.Jlist.setForeground(new java.awt.Color(255, 255, 255));
        lobby.Jlist.setSelectionBackground(new java.awt.Color(57, 60, 65));
        lobby.Jlist.setFixedCellHeight(30);
        lobby.Jlist.setFixedCellWidth(30);
        lobby.Scroll.setViewportView(lobby.Jlist);
    }

    public RoomList getRoomlist() {

        try {
            lobbymodel.setType(1);
            System.out.println("{서버에게 방 목록 요청!!}");
            oos.writeObject(lobbymodel);

            oos.flush();
            oos.reset();

            while (socket != null) {
                System.out.println("{서버에게 방 목록 받기를 기다는 중...}");
                lobbymodel = (LobbyModel) ois.readObject();
                System.out.println("{서버에게 방 목록을 받았음}");

                return (RoomList) lobbymodel.getModel(1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public FriendList getFriendlist() {
        try {
            friendlist.setUser(user);
            lobbymodel.setModel(0, friendlist);
            lobbymodel.setType(0);
            System.out.println("<서버에게 친구 목록 요청>");
            oos.writeObject(lobbymodel);
            oos.flush();
            oos.reset();

            while (socket != null) {
                System.out.println("<서버에게 방 목록 받기를 기다는 중...>");
                lobbymodel = (LobbyModel) ois.readObject();
                System.out.println("<서버에게 친구 목록을 받았음>");
                return (FriendList) lobbymodel.getModel(0);

            }
        } catch (Exception e) {
            System.out.println("친구 목록 받던 중 익셉션");
        }
        return null;
    }

    public int addFriend() {
        LOOP1:
        while (socket != null) {
            try {
                String friend_id = JOptionPane.showInputDialog("친구 등록하고 싶은 아이디");

                if (friend_id == null) {
                    System.out.println("Cancel is pressed");
                    return 0;
                }

                if (friend_id.equals("")) {
                    JOptionPane.showMessageDialog(null, "공백문자는 입력할 수 없습니다!");
                    continue LOOP1;
                }

                if (user.getId().equals(friend_id)) {
                    JOptionPane.showMessageDialog(null, "나 자신은 인생의 영원한 친구입니다.");
                    continue;
                }
                friendInfo.setId(friend_id);
                friendInfo.setType(0);
                friendInfo.setUser(user.getId());
                lobbymodel.setType(2);
                lobbymodel.setModel(2, friendInfo);
                System.out.println("<서버에게 친구 추가 요청>");
                oos.writeObject(lobbymodel);
                oos.flush();
                oos.reset();

                while (socket != null) {
                    lobbymodel = (LobbyModel) ois.readObject();
                    System.out.println("<서버로 부터 친구 등록 결과 받음>");
                    friendInfo = (FriendInfo) lobbymodel.getModel(2);
                    switch (friendInfo.getResult()) {
                        case 1:
                            FriendBtnClicked();
                            return 0;
                        case 2:
                            JOptionPane.showMessageDialog(null, "그런 친구는 없습니다.");
                            continue LOOP1;
                        case 0:
                            JOptionPane.showMessageDialog(null, "이미 등록된 친구입니다.");
                            continue LOOP1;
                    }

                }

            } catch (Exception e) {
                System.out.println("친구 등록 중 익셉션 발생");
                e.printStackTrace();
            }
            return 0;
        }
        return 0;
    }

    public int addRoom() {
        LOOP1: while (socket != null) {
            try {
                String r_name = JOptionPane.showInputDialog("생성하고 싶은 방이름을 적어주세요");
                if (r_name == null){
                    System.out.println("Cancel is pressed");
                    return 0;
                }
                if(r_name.equals("")){
                    JOptionPane.showMessageDialog(null, "공백문자는 입력할 수 없습니다!");
                    continue LOOP1;
                }
                roominfo.setRoomName(r_name);
                lobbymodel.setType(3);
                lobbymodel.setModel(3, roominfo);

                oos.writeObject(lobbymodel);
                oos.flush();
                oos.reset();

                while (socket != null) {
                    lobbymodel = (LobbyModel) ois.readObject();
                    roominfo = (RoomInfo) lobbymodel.getModel(3);
                    System.out.println("방 추가...");
                    switch (roominfo.getType()) {
                        case 0:
                            System.out.println("방 추가...2");
                            RoomBtnClicked();
                            return 0;
                        case 1:
                            System.out.println("방 생성 중 오류!");
                            return 0;
                        default:
                            System.out.println(roominfo.getType());
                            System.out.println("ummm....");
                            return 0;
                    }

                }

            } catch (Exception e) {
                System.out.println("방 등록 중 익셉션 발생");
                e.printStackTrace();
            }
            return 0;
        }
        return 0;
    }

    public void run() {
        lobby.nameLabel.setText(user.getName());
        try {
            socket = new Socket(serverInfo.serverIp, serverInfo.lobbyPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            FriendBtnClicked();
            lobby.setVisible(true);
            //    friendlist = getFriendlist();
            //    roomlist = getRoomlist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
