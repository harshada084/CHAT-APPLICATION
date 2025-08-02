package com.example.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client implements ActionListener {

    JTextField messageInput;
    JPanel messagePanel;
    JFrame frame;
    DataOutputStream dout;
    Socket s;

    Client() {
        frame = new JFrame();
        frame.setLayout(null);

        JPanel top = new JPanel();
        top.setBackground(new Color(7, 94, 84));
        top.setBounds(0, 0, 450, 70);
        top.setLayout(null);
        frame.add(top);

        // Back icon
        ImageIcon backIcon = new ImageIcon("icons/back.png");
        Image backImg = backIcon.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        JLabel back = new JLabel(new ImageIcon(backImg));
        back.setBounds(5, 20, 25, 25);
        top.add(back);
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                System.exit(0);
            }
        });

        // Profile picture
        ImageIcon profileIcon = new ImageIcon("icons/logo.png");
        Image profileImg = profileIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel profileLabel = new JLabel(new ImageIcon(profileImg));
        profileLabel.setBounds(40, 10, 50, 50);
        top.add(profileLabel);

        // Message panel
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setBounds(5, 75, 440, 570);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane);

        // Input field
        messageInput = new JTextField();
        messageInput.setBounds(5, 655, 310, 40);
        frame.add(messageInput);

        // Send button
        ImageIcon sendIcon = new ImageIcon("icons/send.png");
        Image sendImg = sendIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton send = new JButton(new ImageIcon(sendImg));
        send.setBounds(320, 655, 123, 40);
        send.addActionListener(this);
        frame.add(send);

        // Frame settings
        frame.setSize(450, 700);
        frame.setLocation(700, 50);
        frame.setUndecorated(true);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setVisible(true);

        // Socket and stream setup
        try {
            s = new Socket("127.0.0.1", 6001);
            DataInputStream din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            // Graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (dout != null) dout.close();
                    if (din != null) din.close();
                    if (s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            // Message receiving loop
            while (true) {
                String msg = din.readUTF();
                JLabel incoming = new JLabel("Server: " + msg);
                messagePanel.add(incoming);
                messagePanel.revalidate();
                frame.repaint();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String out = messageInput.getText();
            if (!out.trim().isEmpty()) {
                messagePanel.add(new JLabel("You: " + out));
                dout.writeUTF(out);
                messageInput.setText("");
                messagePanel.revalidate();
                frame.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
