import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import org.glassfish.tyrus.server.Server;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@ServerEndpoint("/chat")
public class ChatServer {

    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());
    private static final String FIXED_KEY = "1234567890abcdef"; // 16-byte AES key

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("✅ Client connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String encryptedMessage, Session session) {
        try {
            // Decrypt message for server log
            String decrypted = decrypt(encryptedMessage);
            System.out.println("💬 Decrypted: " + decrypted);

            // Broadcast encrypted message to all clients
            synchronized (clients) {
                for (Session client : clients) {
                    if (client.isOpen()) {
                        client.getBasicRemote().sendText(encryptedMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("❌ Client disconnected: " + session.getId());
    }

    // AES decryption method
    private String decrypt(String encrypted) throws Exception {
        SecretKeySpec key = new SecretKeySpec(FIXED_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16]; // zero IV
        cipher.init(Cipher.DECRYPT_MODE, key, new javax.crypto.spec.IvParameterSpec(iv));
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    public static void main(String[] args) {
        Map<String, Object> properties = new HashMap<>();
        Server server = new Server("0.0.0.0", 8080, "/chat", ChatServerEndpoint.class);

        try {
            server.start();
            System.out.println("🚀 Secure Chat Server started at ws://localhost:8080/chat");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
