/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.UserAccount;
import Model.FriendList;
import Model.RoomList;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import View.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Seok17
 */
public class LobbyController extends Thread {

    UserAccount user;
    FriendList friendlist;
    RoomList roomlist;

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
        roomlist = new RoomList();
        
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

    public void RoomBtnClicked() {
        now = 0;

    }

    public void FriendBtnClicked() {
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
            System.out.println("{서버에게 방 목록 요청}");
            oos.writeObject(roomlist);
            oos.flush();
            oos.reset();
            while (socket != null) {
                System.out.println("{서버에게 방 목록 받기를 기다는 중...}");
                roomlist = (RoomList) ois.readObject();
                System.out.println("{서버에게 친구 목록을 받았음}");
                return roomlist;

            }
        } catch (Exception e) {

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

            friendlist = getFriendlist();
            roomlist = getRoomlist();

        } catch (Exception e) {
            System.out.println("err");
        }
    }
}
