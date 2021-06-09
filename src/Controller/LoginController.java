/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;
import View.LoginViewGUI;
import Model.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Seok17
 */
public class LoginController extends Thread{
    
    LoginViewGUI loginView;
    UserAccount user;
    
    ServerInfo serverInfo;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    static int login = 0, fnacc = 1, signup =3;
    // 로그인 = 0, 계정 찾기 = 1, 회원가입 = 3
    public LoginController() {
        serverInfo = ServerInfo.getInstance();
        loginView = new LoginViewGUI();
        
        user = new UserAccount();
        
        loginView.LoginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 if(loginView.LoginBtn.equals(e.getSource())){
                     LoginBtnClick(e);
      }
            }
        });
        
        loginView.signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 if(loginView.signUpBtn.equals(e.getSource())){
                     signUpBtnClick();
      }
            }
        });
        
         loginView.findAccBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 if(loginView.findAccBtn.equals(e.getSource())){
                     findAccBtnClick();
      }
            }
        });
  }
    
  
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      if(loginView.LoginBtn.equals(evt.getSource())){
          LoginBtnClick(evt);
      }
      else if(loginView.signUpBtn.equals(evt.getSource())){
          signUpBtnClick();
      }
      else if(loginView.findAccBtn.equals(evt.getSource())){
          findAccBtnClick();
      }
      
        
    }
    public void LoginBtnClick(java.awt.event.ActionEvent evt) {             
        user.setId(loginView.idField.getText());
        user.setPw(new String(loginView.PasswordField.getPassword()));
        user.setType(0);
        try{
            oos.writeObject(user);
            oos.flush();
            oos.reset();
            
            boolean chk = true;
            
            Loop1 : while(socket!=null){
                System.out.println("받는거 시작");
                UserAccount recevied = (UserAccount)(ois.readObject());
                System.out.println("다시 받음 암튼");
                if(recevied.getId().equals(user.getId()) && recevied.getPw().equals(user.getPw()) && recevied.getType()==user.getType()){
                    switch(recevied.getChk(login)){
                        case 0:
                            
                            System.out.println("응기잇 로그인 성공임");
                            loginView.dispose();
                            LobbyController lobby = new LobbyController(user);  //로비화면 킴
                            lobby.start();
                            close();
                            break Loop1;                            
                        case 1:  
                            JOptionPane.showMessageDialog(null, "로그인실패 아이디와 패스워드 확인하세요!");
                            loginView.idField.setText("");
                            loginView.PasswordField.setText("");
                            break Loop1;
                    }
                
                }
            }
        }catch(Exception e){
            
        }
    }  
 
    public void signUpBtnClick(){
        SignUpController signup = new SignUpController(socket, ois, oos);
        signup.start();
    }
    
    public void  findAccBtnClick(){
        FindAccountController findAcc = new FindAccountController(socket, oos, ois);
        findAcc.start();
    }
    
    public void close(){  //서버와의 연결을 종료...
        try{
        oos.close();
        ois.close();
        socket.close();
       }catch(Exception e){
                
        }
    }
    
    public void run(){
        loginView.setVisible(true);
        try{
            socket = new Socket(serverInfo.serverIp, serverInfo.loginPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            
        }catch(Exception e){
            System.out.println("err");
        }
    }
    
}
