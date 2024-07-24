package works.weave.socks.cart.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.util.Base64;

public class DataUtils {

    /**
     * 检查与给定URL的连接是否使用了SSL/TLS协议。
     * @param urlStr URL字符串
     * @return boolean
     */
    public boolean checkSecureConnection(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return false;
        } catch (Exception e) {
            // 异常处理：这里可以添加日志记录或其他错误处理逻辑
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        DataUtils checker = new DataUtils();
        // 测试一个使用HTTPS的网站
        System.out.println("Checking https://www.example.com: " + checker.checkSecureConnection("https://www.example.com"));
    }


    /**
     * 获取加密后的密码
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String getEncryptedPassword() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String hardcodedKey = "ThisIsAVeryBadSecretKey"; // 密钥
        String password = "mySuperSecretPassword"; // 密码
        Key secretKey = new SecretKeySpec(hardcodedKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(password.getBytes());
        String encoded = Base64.getEncoder().encodeToString(encrypted);
        System.out.println("Encrypted Password: " + encoded);
        return encoded;
    }

    private static String encryptData(String data, String key) {
        // 使用DES算法加密数据
        try {
            SecureRandom random = new SecureRandom();
            // 这里的SecureRandom使用默认种子
            byte[] iv = new byte[8];
            random.nextBytes(iv);

            // DES算法和ECB模式
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }
}
