import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EncryptionService extends Remote {
    String encrypt(String text, String secretKey) throws RemoteException;
    String decrypt(String encryptedText, String secretKey) throws RemoteException;
}

