package ınstantmessageapplication;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author BECERIKLI
 */
public class Contact {
    private Socket contactSocket;
    private ObjectInputStream contactInput; 
    private ObjectOutputStream contactOutput;
    private javax.swing.JTextPane received_message;
    private javax.swing.JLabel senderName;
    private javax.swing.JTextField phoneNumber;
    private javax.swing.JTextField name;
    private Thread contactThread;

    protected void sendMessage(String message) throws IOException {
        // gelen mesajı (metin, resim ve pdf) server'a gönder
        
    }
    
    protected void start(String host, int port, javax.swing.JTextField txt_phoneNumber, javax.swing.JTextField txt_name) throws IOException {
        // client soketi oluşturma (ip + port numarası)
        contactSocket = new Socket(host, port);
        this.phoneNumber = txt_phoneNumber;
        this.name = txt_name;
        // input  : client'a gelen mesajları okumak için
        // output : client'dan bağlı olduğu server'a mesaj göndermek için
        contactOutput = new ObjectOutputStream(contactSocket.getOutputStream());
        contactInput = new ObjectInputStream(contactSocket.getInputStream());

        // server'ı sürekli dinlemek için Thread oluştur
        contactThread = new ListenThread();
        contactThread.start();
    }

  /*  protected void writeToHistory(Object message) {
        // client arayüzündeki history alanına mesajı yaz
        received_message.setText(received_message.getText() + "\n" + message);
    }*/

    protected void disconnect() throws IOException {
        // bütün streamleri ve soketleri kapat
        if (contactInput != null) {
            contactInput.close();
        }
        if (contactOutput != null) {
            contactOutput.close();
        }
        if (contactThread != null) {
            contactThread.interrupt();
        }
        if (contactSocket != null) {
            contactSocket.close();
        }
    }
    
    protected void updatePort(int port) {
        contactSocket.getPort(); // buna da bakacağuk
    }
    

    class ListenThread extends Thread {

        // server'dan gelen mesajları dinle
        @Override
        public void run() {
            try {
//                writeToHistory("Sohbet başlatıldı!");

                Object mesaj;
                // server mesaj gönderdiği sürece gelen mesajı al
                while ((mesaj = contactInput.readObject()) != null) {
                    // id mesajı kontrolü, id mesajı alınırsa name etiketini değiştirir 
                    if (mesaj instanceof String) {
                   //     writeToHistory(mesaj);  // serverdan gelen mesajı arayüze yaz
                    }

                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error - ListenThread : " + ex);
            }
        }
    }
}
