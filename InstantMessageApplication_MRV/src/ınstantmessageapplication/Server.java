package ınstantmessageapplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BECERIKLI
 */
public class Server {

    private ServerSocket serverSocket;
    private javax.swing.JTextPane historyJTextPane;
    private Thread serverThread;
    private HashSet<ObjectOutputStream> allContacts = new HashSet<>();

    protected void sendMessage(String message) throws IOException {
        // buna bakacoğuz..

    }

    protected void start(int port) throws IOException {
        // server soketi oluşturma (sadece port numarası)
        serverSocket = new ServerSocket(port);
        System.out.println("Server başlatıldı ..");

        // server arayüzündeki history alanı, bütün olaylar buraya yazılacak
        // arayüzü kitlememek için, server yeni client bağlantılarını ayrı Thread'de beklemeli
        serverThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    // blocking call, yeni bir client bağlantısı bekler
                    Socket contactSocket = serverSocket.accept();
                    System.out.println("Yeni bir kullanıcı bağlandı : " + contactSocket);

                    // bağlanan her client için bir thread oluşturup dinlemeyi başlat
                    new ListenThread(contactSocket).start();
                } catch (IOException ex) {
                    System.out.println("Hata - new Thread() : " + ex);
                    break;
                }
            }
        });
        serverThread.start();
    }

    protected void stop() throws IOException {
        // bütün streamleri ve soketleri kapat
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    class ListenThread extends Thread {

        // dinleyeceğimiz client'ın soket nesnesi, input ve output stream'leri
        private final Socket contactSocket;
        private ObjectInputStream contactInput;
        private ObjectOutputStream contactOutput;

        private ListenThread(Socket contactSocket) {
            this.contactSocket = contactSocket;
        }

        @Override
        public void run() {
            System.out.println("Bağlanan client için thread oluşturuldu : " + this.getName());

            try {
                // input  : client'dan gelen mesajları okumak için
                // output : server'a bağlı olan client'a mesaj göndermek için
                contactInput = new ObjectInputStream(contactSocket.getInputStream());
                contactOutput = new ObjectOutputStream(contactSocket.getOutputStream());

                // Bütün client'lara yeni katılan client bilgisini gönderir
                for (ObjectOutputStream out : allContacts) {
                    out.writeObject(this.getName() + " server'a katıldı.");
                }

                // broadcast için, yeni gelen client'ın output stream'ını listeye ekler
                allContacts.add(contactOutput);

                // client ismini mesaj olarak gönder
                contactOutput.writeObject("@id-" + this.getName());

                Object mesaj;
                // client mesaj gönderdiği sürece mesajı al
                while ((mesaj = contactInput.readObject()) != null) {
                    // client'in gönderdiği mesajı server ekranına yaz
                    System.out.println(this.getName() + " : " + mesaj);

                    // bütün client'lara gelen bu mesajı gönder
                    for (ObjectOutputStream out : allContacts) {
                        out.writeObject(this.getName() + ": " + mesaj);
                    }

                    // "son" mesajı iletişimi sonlandırır
                    if (mesaj.equals("son")) {
                        break;
                    }
                }

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Hata - ListenThread : " + ex);
            } finally {
                try {
                    // client'ların tutulduğu listeden çıkart
                    allContacts.remove(contactOutput);

                    // bütün client'lara ayrılma mesajı gönder
                    for (ObjectOutputStream out : allContacts) {
                        out.writeObject(this.getName() + " server'dan ayrıldı.");
                    }

                    // bütün streamleri ve soketleri kapat
                    if (contactInput != null) {
                        contactInput.close();
                    }
                    if (contactOutput != null) {
                        contactOutput.close();
                    }
                    if (contactSocket != null) {
                        contactSocket.close();
                    }
                    System.out.println("Soket kapatıldı : " + contactSocket);
                } catch (IOException ex) {
                    System.out.println("Hata - Soket kapatılamadı : " + ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server;
        
        int port = 11111;
        server = new Server();
        
        try {
            server.start(port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
