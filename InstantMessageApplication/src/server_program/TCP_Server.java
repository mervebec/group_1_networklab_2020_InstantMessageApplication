package server_program;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @file TCP_Server.java
 * @date Feb 17, 2020 , 12:28:55
 * @author Muhammet Alkan
 */
public class TCP_Server {

    private ServerSocket serverSocket;
    private javax.swing.JTextPane historyJTextPane;
    private Thread serverThread;
    private final ArrayList<Client> allClients = new ArrayList<>();

    protected void creatClient(Client newclient) throws IOException {

        boolean exsit = false;
        for (Client client : allClients) {// check if server has this client by phone no which is the uniq about every client
            if (client.telefon == newclient.telefon) {
                exsit = true;
            }
        }
        if (!exsit) {

            newclient.state = "log-in";
            allClients.add(newclient);
            newclient.outputstream.writeObject("Created");

        } else {

            newclient.outputstream.writeObject("This telefon already exist!");
                System.out.println("var");

        }
    }

    protected void start(int port, javax.swing.JTextPane jTextPaneHistory) throws IOException {
        // server soketi oluşturma (sadece port numarası)
        serverSocket = new ServerSocket(port);
        System.out.println("Server başlatıldı ..");

        // server arayüzündeki history alanı, bütün olaylar buraya yazılacak
        this.historyJTextPane = jTextPaneHistory;

        // arayüzü kitlememek için, server yeni client bağlantılarını ayrı Thread'de beklemeli
        serverThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    // blocking call, yeni bir client bağlantısı bekler
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Yeni bir client bağlandı : " + clientSocket);

                    // bağlanan her client için bir thread oluşturup dinlemeyi başlat
                    new ListenThread(clientSocket).start();
                } catch (IOException ex) {
                    System.out.println("Hata - new Thread() : " + ex);
                    break;
                }
            }
        });
        serverThread.start();
    }

    protected void sendmsg(String message, Client client) throws IOException {
        // bütün bağlı client'lara mesaj gönder
        client.outputstream.writeObject("Server : " + message);

    }

    protected void sendBroadcast(String message) throws IOException {
        // bütün bağlı client'lara mesaj gönder
        for (Client client : allClients) {
            client.outputstream.writeObject("Server : " + message);
        }
    }

    protected void writeToHistory(String message) {
        // server arayüzündeki history alanına mesajı yaz
        historyJTextPane.setText(historyJTextPane.getText() + "\n" + message);
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
        private final Socket clientSocket;
        private ObjectInputStream clientInput;
        private ObjectOutputStream clientOutput;
        private String UI;

        private ListenThread(Socket clientSocket) {
            this.clientSocket = clientSocket;

        }

        @Override
        public void run() {
            writeToHistory("Thread has ben created for connected client : " + this.getName());
            writeToHistory("Bağlanan client için thread oluşturuldu : " + this.getName());

            try {
                // input  : client'dan gelen mesajları okumak için
                // output : server'a bağlı olan client'a mesaj göndermek için
                clientInput = new ObjectInputStream(clientSocket.getInputStream());
                clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());

                // client ismini mesaj olarak gönder
                //clientOutput.writeObject("@id-" + this.getName());
                Object mesaj;
                // client mesaj gönderdiği sürece mesajı al

                Client client_info1 = null;
                System.out.println("mesaj instanceof Client");

                while ((mesaj = clientInput.readObject()) != null) {
                    if (mesaj instanceof Client) {
                        System.out.println(mesaj instanceof Client);
                        Client client_info = (Client)mesaj;
                        client_info.outputstream = clientOutput;
                        client_info.inputStream = clientInput;
                        client_info1 = client_info;
                    }
                    // client'in gönderdiği mesajı server ekranına yaz  

                    if (mesaj.equals("Creat Client")) {// when sing-up button is pressed the client will send to the server this msg 
                        System.out.println(client_info1.name);
                        creatClient(client_info1);
                    }
                    writeToHistory(" : " + mesaj);

                }

                for (Client client : allClients) {
                    if (client.outputstream == this.clientOutput) {
                        writeToHistory("Username: " + client.telefon + "  Name: " + client.name);

                    }
                }

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Hata - ListenThread : " + ex);
            } finally {
                try {
                    // client'ların tutulduğu listeden çıkart
                    //    allClients.remove(clientOutput);

                    // bütün client'lara ayrılma mesajı gönder
                    // bütün streamleri ve soketleri kapat
                    if (clientInput != null) {
                        clientInput.close();
                    }
                    if (clientOutput != null) {
                        clientOutput.close();
                    }
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                    writeToHistory("Soket kapatıldı : " + clientSocket);
                } catch (IOException ex) {
                    System.out.println("Hata - Soket kapatılamadı : " + ex);
                }
            }
        }
    }

}
