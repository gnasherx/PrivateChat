import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface PrivateChatClientInterface extends Remote {
    void sendMessageToClient(String message) throws RemoteException;

    void getAllClientList(Vector<String> clients) throws RemoteException;

    void areYouChatingWith(String name, PrivateChatClientInterface offlineClientRef) throws RemoteException;
}
