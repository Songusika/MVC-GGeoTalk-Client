/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.RoomInfo;
import Model.UserAccount;
import Model.MessageInfo;


import View.FileChooser;
import View.ChattingRoomView1;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;
import java.net.*;
import javax.swing.JScrollBar;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author Seok17
 */
public class ChatController extends Thread {

    RoomInfo room;
    UserAccount user;
    MessageInfo msg;
    ChattingRoomView1 chat;
    BufferedImage img;
    public static SimpleDateFormat date = new SimpleDateFormat("kk시 mm분");

    Socket socket;
    ServerInfo server = ServerInfo.getInstance();

    ObjectOutputStream oos;
    ObjectInputStream ois;

    public ChatController(RoomInfo room, UserAccount user) {
        chat = new ChattingRoomView1(room.getRoomName());
        this.room = room;
        this.user = user;
        chat.displayroom.setText(room.getRoomName());
        try {
            socket = new Socket(server.serverIp, server.chatPort);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            chat.setLocationRelativeTo(null);
            chat.setVisible(true);

            Thread Sender = new Thread(new ClientSender(socket, chat, room, user));
            Thread Receiver = new Thread(new ClientReceiver(socket, chat, room, user));

            Sender.start();
            Receiver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    class ClientSender extends Thread {

        RoomInfo room;
        UserAccount user;
        MessageInfo msg;
        ChattingRoomView1 chat;
        File file;

        Socket socket;
        ObjectOutputStream oos;

        public ClientSender(Socket socket, ChattingRoomView1 chat, RoomInfo room, UserAccount user) {
            this.socket = socket;
            this.chat = chat;
            this.room = room;
            this.user = user;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            chat.sendMsg.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMsg();
                }
            });

            chat.sendImg.addActionListener(new ActionListener() {
                @Override
                synchronized public void actionPerformed(ActionEvent e) {
                    file = FileChooser.showFile();
                    ImageIcon icon = new ImageIcon(file.getPath());
                     JOptionPane.showMessageDialog(
                        null,
                        "Hello world",
                        "Hello", JOptionPane.INFORMATION_MESSAGE,
                        icon);
                    System.out.println(".actionPerformed()");

                    
                    Path path = file.toPath();
                    System.out.println("path.actionPerformed()");
                    byte[] bytes = null;
                    System.out.println("byte.actionPerformed()");
                    try {
                        bytes = Files.readAllBytes(path);
                        System.out.println("file.actionPerformed()");
                    } catch (IOException ex) {
                        Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    
                    System.out.println("msg.actionPerformed()");
                    sendImg(bytes);
                }
            });

            chat.typingArea.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        sendMsg();
                    }
                }
            });

        }

        public void run() {

        }

        public void sendMsg() {
            msg = new MessageInfo();
            msg.setMessage(chat.typingArea.getText());
            msg.setType(0);
            msg.setName(user.getName());
            msg.setId(room.getroomId());
            Date d = new Date();
            msg.setTime(date.format(d));

            try {
                oos.writeObject(msg);
                oos.flush();
                oos.reset();

                chat.typingArea.setText("");
                System.out.println("메시지를 잘 보냈습니다.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendImg(byte[] file) {
            msg = new MessageInfo();
            msg.setFile(file);
            msg.setType(1);
            msg.setId(room.getroomId());
            msg.setName(user.getName());
            Date d = new Date();
            msg.setTime(date.format(d));

            try {
                oos.writeObject(msg);
                oos.flush();
                oos.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class ClientReceiver extends Thread {

        RoomInfo room;
        UserAccount user;
        MessageInfo msg;
        ChattingRoomView1 chat;
        File file;

        Socket socket;
        ObjectInputStream ois;

        public ClientReceiver(Socket socket, ChattingRoomView1 chat, RoomInfo room, UserAccount user) {

            this.socket = socket;
            this.chat = chat;
            this.room = room;
            this.user = user;
            msg = new MessageInfo();
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void repaint() {
            chat.jScrollPane1.setViewportView(chat.ChattingArea);
            JScrollBar scr = chat.jScrollPane1.getVerticalScrollBar();
            if (chat.stack_height > 448) {
                scr.setValue(chat.stack_height);
            }
        }

        public void showMsg(MessageInfo msg, int type) {
            int me = type;
            switch (msg.getType()) {
                case 1:

                    chat.ChatMsgView(msg.getName(), msg.getMessage(), msg.getTime(), me);
                    repaint();
                    break;

                case 0:
                    chat.ChatMsgView(msg.getName(), msg.getMessage(), msg.getTime(), me);
                    repaint();
                    break;
            }
        }

        public void showImg(MessageInfo msg, int type) {
            int me = type;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(msg.getFile());
            {
                try {
                    img = ImageIO.read(inputStream);
                } catch (IOException ex) {
                    Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            switch (msg.getType()) {
                case 1: {

                    chat.ChatImgView(msg.getName(), msg.getTime(), me, img);
                }
                repaint();
                break;

                case 0:
                    chat.ChatImgView(msg.getName(), msg.getTime(), me, img);
                    repaint();
                    break;
            }
        }

        public void run() {
            System.out.println("야 실행 됬냐?");
            try {
                while (socket != null) {
                    msg = new MessageInfo();
                    System.out.println("메시지 받는 것을 기다리는 중 !");
                    msg = (MessageInfo) ois.readObject();
                    System.out.println("메시지를 받았습니다 !");
                    System.out.println(msg.getId() + " \\ " + room.getroomId());
                    System.out.println(msg.getMessage());
                    if (msg.getId() == room.getroomId()) {
                        switch (msg.getType()) {
                            case 0:   //문자열
                                if (msg.getName().equals(user.getName())) {
                                    showMsg(msg, 1);

                                } else {
                                    showMsg(msg, 0);
                                }
                                break;
                            case 1: //사진 
                                if (msg.getName().equals(user.getName())) {
                                    showImg(msg, 1);

                                } else {
                                    showImg(msg, 0);
                                }
                                break;

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
