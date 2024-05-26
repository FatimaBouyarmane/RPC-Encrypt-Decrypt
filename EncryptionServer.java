import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptionServer extends UnicastRemoteObject implements EncryptionService {
    private static final String AES_ALGORITHM = "AES";
    private static final Logger LOGGER = Logger.getLogger(EncryptionServer.class.getName());

    protected EncryptionServer() throws RemoteException {
        super();
    }

    @Override
    public String encrypt(String text, String secretKey) throws RemoteException {
        return performOperation(text, secretKey, true);
    }

    @Override
    public String decrypt(String encryptedText, String secretKey) throws RemoteException {
        return performOperation(encryptedText, secretKey, false);
    }

    private String performOperation(String text, String secretKey, boolean encrypt) {
        try {
            if (secretKey.length() != 16 && secretKey.length() != 24 && secretKey.length() != 32) {
                throw new IllegalArgumentException("Invalid AES key length (must be 16, 24, or 32 bytes)");
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            if (encrypt) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                byte[] outputBytes = cipher.doFinal(text.getBytes());
                return Base64.getEncoder().encodeToString(outputBytes);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                byte[] outputBytes = cipher.doFinal(Base64.getDecoder().decode(text));
                return new String(outputBytes);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error performing operation", e);
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        try {
            EncryptionServer server = new EncryptionServer();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("EncryptionService", server);
            LOGGER.info("Server started.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Server exception", e);
        }
    }
}
