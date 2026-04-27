import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String ALGO = "AES";
    
    // 🔑 Shared key (must be 16 characters = 128-bit)
    private static final String FIXED_KEY = "1234567890abcdef";

    public static String encrypt(String data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(FIXED_KEY.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encVal = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encVal);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(FIXED_KEY.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
            byte[] decValue = cipher.doFinal(decodedValue);
            return new String(decValue);
        } catch (Exception e) {
            return "⚠️ Error decrypting message";
        }
    }
}