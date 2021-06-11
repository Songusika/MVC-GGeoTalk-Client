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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import View.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JList;
/**
 *
 * @author Seok17
 */
public class LobbyController extends Thread {

    UserAccount user;
    FriendList friendlist;
    FriendInfo friendInfo;
    RoomList roomlist;
    LobbyModel lobbymodel;
    
    

    ServerInfo serverInfo = ServerInfo.getInstance();
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    LobbyView lobby;
    int now = 1; //0 이면 친구창, 1이면 방리스트창  
    static int GETROOM = 1, GETFRIEND = 0;

    public LobbyController(UserAccount user) {
        this.user = user;
        lobby = new LobbyView();
        friendlist = new FriendList();
        friendInfo = new FriendInfo();
        roomlist = new RoomList();
        lobbymodel = new LobbyModel();
        
        
        lobby.plusBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lobby.plusBtn.equals(e.getSource())) {

                }
            }
        });

        lobby.refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lobby.refreshBtn.equals(e.getSource())) {
                    roomlist = getRoomlist();
                    
                    roomlist.getlen();
                }
            }
        });
        lobby.showFriendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lobby.showFriendBtn.equals(e.getSource())) {

                }
            }
        });
        lobby.showRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lobby.showRoomBtn.equals(e.getSource())) {

                }
            }
        });

    }
    
    public void mouseClicked(MouseEvent evt){
        JList list = (JList)evt.getSource();
        if(evt.getClickCount() == 2){
            new ChattingRoomView();
        }
    }

    public void RoomBtnClicked() { //JList 업데이트 해야함. --> 방목록으로 
        now = 0;

    }

    public void FriendBtnClicked() { //JList 업데이트 해야함. --> 친구목록으로 
        now = 1;

    }

    public void RefreshBtnClicked() {
        switch (now) {
            case 0:

            case 1:

        }
    }

    public void addBtnClicked() {
        switch (now) {
            case 0:

            case 1:

        }
    }
    
    public RoomList getRoomlist() {

        try {
            lobbymodel.setType(1);
            System.out.println("{서버에게 방 목록 요청!!}");
            oos.writeObject(lobbymodel);
            System.err.println("보냄?");
            oos.flush();
            oos.reset();
            System.out.println("보내기 성공 !");
            while (socket != null) {
                System.out.println("{서버에게 방 목록 받기를 기다는 중...}");
                lobbymodel = (LobbyModel) ois.readObject();
                System.out.println("{서버에게 방 목록을 받았음}");
                
                return (RoomList)lobbymodel.getModel(1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public FriendList getFriendlist() {
        try {
            System.out.println("<서버에게 친구 목록 요청>");
            oos.writeObject(friendlist);
            oos.flush();
            oos.reset();

            while (socket != null) {
                System.out.println("<서버에게 방 목록 받기를 기다는 중...>");
                friendlist = (FriendList) ois.readObject();
                System.out.println("<서버에게 친구 목록을 받았음>");
                return friendlist;

            }
        } catch (Exception e) {

        }
        return null;
    }

    public void run() {
        lobby.setVisible(true);
        try {
            socket = new Socket(serverInfo.serverIp, serverInfo.lobbyPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        //    friendlist = getFriendlist();
        //    roomlist = getRoomlist();

        } catch (Exception e) {
            System.out.println("err");
        }
    }
}
