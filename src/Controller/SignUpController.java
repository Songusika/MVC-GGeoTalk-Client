/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.UserAccount;
import View.SignUpView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Seok17
 */
public class SignUpController {

    SignUpView signupView;
    UserAccount user;

    ServerInfo serverInfo;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public SignUpController() {
        serverInfo = ServerInfo.getInstance();
        signupView = new SignUpView();
        signupView.setVisible(true);
        
        signupView.checkID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 if(signupView.checkID.equals(e.getSource())){
                     //LoginBtnClick(e);
      }
            }
        });
    }

    public void run() {

    }

}
