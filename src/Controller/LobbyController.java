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
/**
 *
 * @author Seok17
 */
public class LobbyController extends Thread{
    
    UserAccount user;
    FriendList friendlist;
    RoomList roomlist;
    
    ServerInfo serverInfo = ServerInfo.getInstance();
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    
    LobbyView lobby;
    static int GETROOM = 0, GETFRIEND = 1;
    
    public LobbyController(UserAccount user){
        this.user = user;
        lobby = new LobbyView();
    }
    
    
    public void RoomBtnClicked(){
        
    }
    
    public void FriendBtnClicked(){
        
    }
    
    public void RefreshBtnClicked(){
        
    }
    
    public void addBtnClicked(){
        
    }
    
    
    
    
    public void run(){
        lobby.setVisible(true);
        try{
            socket = new Socket(serverInfo.serverIp, serverInfo.lobbyPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            
        }catch(Exception e){
            System.out.println("err");
        }
    }
    }

