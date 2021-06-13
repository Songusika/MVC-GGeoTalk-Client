/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 *
 * @author 권대철
 */
public class imgpreview {
    public static void preview(Image img) {
        JPanel em = new JPanel() {
            public void paintComponent(Graphics g) {
                Dimension d = new Dimension(300, 280);
                g.drawImage(new ImageIcon(img).getImage(), 0, 0, d.width, d.height, null);
            }
        };
        JFrame f = new JFrame("이미지 그리기");
        JButton btn = new JButton("확인");
        JPanel background = new JPanel();
        f.add(background);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(300, 300));
        em.setPreferredSize(
                new Dimension(300, 280));
        // f.getContentPane()
        //      .add(em);
        //f.add(btn);
        background.setLayout(new BorderLayout());
        background.add(em, BorderLayout.CENTER);
        background.add(btn, BorderLayout.SOUTH);
        //f.setLayout(
        //      new FlowLayout());
        f.pack();
        f.setLocation(
                200, 200);
        f.setVisible(
                true);
    }
}