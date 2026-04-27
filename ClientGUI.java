import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;

    public ClientGUI() {
        setTitle("💬 Secure Chat - Client");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        inputField = new JTextField();
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(e -> sendMessage());

        connectToServer();
        setVisible(true);
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 5000);
                chatArea.append("✅ Connected to Secure Chat Server\n");
                out = new PrintWriter(socket.getOutputStream(), true);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg;
                while ((msg = in.readLine()) != null) {
                    String decrypted = AESUtil.decrypt(msg);
                    chatArea.append("\n📩 Encrypted: " + msg + "\n💬 Decrypted: " + decrypted + "\n");
                }
            } catch (IOException e) {
                chatArea.append("❌ Connection failed\n");
            }
        }).start();
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            String encrypted = AESUtil.encrypt(message);
            out.println(encrypted);
            chatArea.append("\n🧑‍💻 You: " + message + "\n🔒 Encrypted: " + encrypted + "\n");
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}
