/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import View.FindAccountView;
import Model.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

/**
 *
 * @author 권대철
 */
public class FindAccountController extends Thread {

    FindAccountView findAccountView;
    UserAccount user;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    ServerInfo serverInfo;
    static int login = 0, fnacc = 1, signup =3;

    public FindAccountController(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        this.socket = socket;
        this.oos = oos;
        this.ois = ois;
        findAccountView = new FindAccountView();
        
        

        findAccountView.chkPwBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (findAccountView.chkPwBtn.equals(e.getSource())) {
                    chkPwBtnClick(e);
                }
            }
        });

    }


    public void chkPwBtnClick(java.awt.event.ActionEvent evt) {
        user = new UserAccount(); //객체는 사용하려면 생성해야 한다 처음 생성한 객체를 또 붙잡고 수정이 잘 안된다.
        System.out.println("클릭시 도는 비번 찾기 함수 시작");
        String id = findAccountView.idField.getText();
        String name = findAccountView.nameField.getText();
        if (!(id.equals("") || name.equals(""))) {
            System.out.println("두 필드 차야 데이터 삽입 시작");
            user.setId(id);
            user.setName(name);
            user.setType(1); //계정찾기 타입 1
        } else {
            JOptionPane.showMessageDialog(null, "똑바로 입력하세요.");
            return;
        }
        System.out.println("객체 전송 함수 시작");

        try {
            oos.writeObject(user);
            oos.flush();
            boolean chk = true;
            Loop2:
            while (socket != null) {
                System.out.println("객체 보냈고 받는거 시작함");

                UserAccount recevied = (UserAccount) (ois.readObject());
                System.out.println("객체 잘 돌려받았음");
                System.out.println(recevied.getChk(fnacc));
                if (recevied.getId().equals(user.getId())  && recevied.getType() == user.getType()) {
                    switch (recevied.getChk(fnacc)) { //1 타입의 finPw의 값 0 ,1
                        case 0:

                            JOptionPane.showMessageDialog(null, "그런 사람 없습니다. 다시 입력해보세요.");
                            break Loop2;
                        case 1:
                            JOptionPane.showMessageDialog(null, "당신의 비밀번호는"+recevied.getPw()+"입니다.");
                            break Loop2;
                        
                    }
                }
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        }        // TODO add your handling code here:
    }

    public void run() {
        try {
            findAccountView.setVisible(true);
        } catch (Exception e) {
            System.out.println("err");
        }
    }

}