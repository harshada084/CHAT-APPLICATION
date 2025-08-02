package com.example.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Server implements ActionListener {

    JTextField messageInput;
    JPanel messagePanel;
    static JFrame frame = new JFrame();
    static DataOutputStream dout;

    Server() {
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

        // Profile picture icon
        ImageIcon profileIcon = new ImageIcon("icons/profile.png");
        Image profileImg = profileIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel profileLabel = new JLabel(new ImageIcon(profileImg));
        profileLabel.setBounds(40, 10, 50, 50);
        top.add(profileLabel);

        // Exit on back click
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                System.exit(0);
            }
        });

        // Message panel setup
        messagePanel = new JPanel();
        messagePanel.setBounds(5, 75, 440, 570);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setBounds(5, 75, 440, 570);
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
        frame.setLocation(200, 50);
        frame.setUndecorated(true);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String out = messageInput.getText();
            if (!out.trim().isEmpty()) {
                messagePanel.add(new JLabel("You: " + out));
                dout.writeUTF(out);
                messageInput.setText("");
                frame.validate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();

        try (
            ServerSocket skt = new ServerSocket(6001);
            Socket s = skt.accept();
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream())
        ) {
            dout = dos;

            // Server receives messages
            while (true) {
                String msg = din.readUTF();
                JLabel received = new JLabel("Client: " + msg);
                server.messagePanel.add(received);
                frame.validate();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
