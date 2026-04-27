import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerGUI extends JFrame {
    private JTextArea chatArea;
    private java.util.List<Socket> clients = new ArrayList<>();

    public ServerGUI() {
        setTitle("🔐 Secure Chat - Server");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        setVisible(true);
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(5000)) {
                chatArea.append("✅ Server started on port 5000\n");

                while (true) {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    chatArea.append("💻 Client connected: " + client.getInetAddress() + "\n");

                    new Thread(() -> handleClient(client)).start();
                }
            } catch (IOException e) {
                chatArea.append("❌ Error: " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void handleClient(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            String encryptedMessage;
            while ((encryptedMessage = in.readLine()) != null) {
                String decrypted = AESUtil.decrypt(encryptedMessage);
                chatArea.append("\n🔒 Encrypted: " + encryptedMessage + "\n💬 Decrypted: " + decrypted + "\n");
                broadcast(encryptedMessage, client);
            }
        } catch (IOException e) {
            chatArea.append("⚠️ Client disconnected\n");
        }
    }

    private void broadcast(String message, Socket sender) {
        for (Socket client : clients) {
            if (client != sender) {
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
