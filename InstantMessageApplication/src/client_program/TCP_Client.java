package client_program;

import java.awt.Color;
import server_program.Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @file TCP_Client.java
 * @date Feb 17, 2020 , 13:07:59
 * @author Muhammet Alkan
 */
public class TCP_Client {

    private final int serverPort = 44444;
    private static Socket clientSocket;
    private static ObjectInputStream clientInput;
    private static ObjectOutputStream clientOutput;
    private javax.swing.JTextPane historyJTextPane;
    private static javax.swing.JFrame jFrame;

    private static javax.swing.JLabel NewRegisterSatuJLabel;
    private Thread clientThread;

    protected void sing_up_to_server(Client client, javax.swing.JLabel jLabelName, javax.swing.JFrame jframe) throws IOException {
       this.NewRegisterSatuJLabel = jLabelName;
        jFrame = jframe;  
        sendMessage(client);// send the client you want to creat 
 
        sendMessage("Creat Client");//say to server that you need to creat client


    }

    protected void log_in_to_server(Client client, javax.swing.JLabel jLabelName, javax.swing.JFrame jframe) throws IOException {

    }

    protected void start(InetAddress inetAddress) throws IOException {
        // client soketi oluşturma (ip + port numarası)

        clientSocket = new Socket(inetAddress, serverPort);
        // client arayüzündeki history alanı, bütün olaylar buraya yazılacak
        //this.historyJTextPane = jTextPaneHistory;
        // to write the registration satute in case of creating exsit client .

        // input  : client'a gelen mesajları okumak için
        // output : client'dan bağlı olduğu server'a mesaj göndermek için
        clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        clientInput = new ObjectInputStream(clientSocket.getInputStream());

        // server'ı sürekli dinlemek için Thread oluştur
        clientThread = new ListenThread();
        clientThread.start();
    }

    protected void sendMessage(Object message) throws IOException {
        // gelen mesajı server'a gönder
        clientOutput.writeObject(message);
    }

    protected void sendObject(Object message) throws IOException {
        // gelen nesneyi server'a gönder
        clientOutput.writeObject(message);
    }

    protected void writeToHistory(Object message) {
        // client arayüzündeki history alanına mesajı yaz
        historyJTextPane.setText(historyJTextPane.getText() + "\n" + message);
    }

    protected void disconnect() throws IOException {
        // bütün streamleri ve soketleri kapat
        if (clientInput != null) {
            clientInput.close();
        }
        if (clientOutput != null) {
            clientOutput.close();
        }
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    class ListenThread extends Thread {

        // server'dan gelen mesajları dinle
        @Override
        public void run() {
            try {

                Object mesaj;
                // server mesaj gönderdiği sürece gelen mesajı al
                while ((mesaj = clientInput.readObject()) != null) {

                    // serverdan gelen mesajı arayüze yaz
                    if (mesaj.equals("This telefon already exist!")) {
                        System.out.println(mesaj);
                        NewRegisterSatuJLabel.setText(mesaj + "");

                    }
                    if (mesaj.equals("Created")) {
                                                System.out.println(mesaj);

                        NewRegisterSatuJLabel.setText("Done");
                        NewRegisterSatuJLabel.setForeground(Color.green);
                        JOptionPane.showMessageDialog(null, "Successfully singed-up !");

                        jFrame.setVisible(false);
                        new main_UI().setVisible(true);

                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error - ListenThread : " + ex);
            }
        }
    }

}
