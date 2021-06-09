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
import javax.swing.JOptionPane;

/**
 *
 * @author Seok17
 */
public class SignUpController extends Thread {

    SignUpView signupView;
    UserAccount user;

    ServerInfo serverInfo;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    boolean chkID = false; //false면, id 체크 안됨 true면 id 체크 후, 문제 없음
    boolean pwChk = false; //false면 비번 체크 안됨. 
    boolean isNull = true; //true면 null 있음,

    public SignUpController(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        this.socket = socket;
        this.oos = oos;
        this.ois = ois;
        serverInfo = ServerInfo.getInstance();
        signupView = new SignUpView();

        user = new UserAccount();

        signupView.checkID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signupView.checkID.equals(e.getSource())) {
                    checkID();
                }
            }
        });

        signupView.goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signupView.goBack.equals(e.getSource())) {
                    backLogin();
                }
            }
        });

        signupView.LoginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signupView.LoginBtn.equals(e.getSource())) {
                    signup();
                }
            }
        });
    }

    public void checkID() {
        if (signupView.IdField.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "빈칸은 사용할 수 없습니다.");
        } else {
            user = new UserAccount();
            user.setType(2);
            user.setId(signupView.IdField.getText());
            System.out.println(user.getId());
            try {
                oos.writeObject(user);
                oos.flush();
                System.out.println("============================");
                System.out.println("보낸 아이디"+user.getId());
                System.out.println("해쉬 코드 : "+ System.identityHashCode(user));
                boolean chk = true;
                
                while (socket != null) {
                    UserAccount recevied = (UserAccount) (ois.readObject());
                    //System.out.println(recevied.getChk(2));
                    if (recevied.getChk(2) == 0) {
                        JOptionPane.showMessageDialog(null, "이미 있는 아이디입니다");
                        signupView.IdField.setText("");
                        chkID = false;
                        break;
                    } else{
                        JOptionPane.showMessageDialog(null, "사용할 수 있는 아이디입니다.");
                        chkID = true;
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("중복확인에서 익셉션");
            }
            
        }
        System.out.println("===================================="); 
    }

    public void backLogin() {
        signupView.dispose();
    }

    public void signup() {
        nullchk();
        if (isNull == false && pwChk == true && chkID == true) {
            //서버와 연결해서 보내야함.
            user = new UserAccount();
            user.setId(signupView.IdField.getText());
            user.setPw(new String(signupView.PwField.getPassword()));
            user.setName(signupView.nameField.getText());
            user.setType(3);

            try {
                oos.writeObject(user);
                oos.flush();
                JOptionPane.showMessageDialog(null, "회원가입 성공!");
                signupView.dispose();
            } catch (Exception e) {
                System.out.println("회원가입에서 익셉션");
            }
        } else if (isNull == true) {
            JOptionPane.showMessageDialog(null, "공백문자는 입력할 수 없습니다.");
        } else if (isNull == false && pwChk == true && chkID == false) {
            JOptionPane.showMessageDialog(null, "아이디 중복 확인을 해주세요.");
        }
    }

    public void nullchk() {
        if (signupView.IdField.getText().equals("") || signupView.nameField.getText().equals("")
                || new String(signupView.PwField.getPassword()).equals("") || new String(signupView.PwCheckField.getPassword()).equals("")) {
            isNull = true;
            equalCheckPw();
        } else {
            isNull = false;
            equalCheckPw();
        }
    }

    public void equalCheckPw() {
        if (!(new String(signupView.PwField.getPassword()).equals(new String(signupView.PwCheckField.getPassword())))) {
            pwChk = false;
            signupView.PwCheckField.setText("");
            signupView.checkPw.setText("비밀번호가 일치하지 않습니다!! ");
        } else {
            pwChk = true;
        }
    }

    public void run() {
        signupView.setVisible(true);
    }

}
