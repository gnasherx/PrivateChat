import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrivateChatServerInterface extends Remote {
    void login(String name, PrivateChatClientInterface ref) throws RemoteException;

    void sendMessageToServer(String message, PrivateChatClientInterface clientRef) throws RemoteException;

    // TODO: Client will send the name, with which he wants to chat
    // TODO: In the sendClinetRef serch for key of this name and send the clientRef
    PrivateChatClientInterface sendClientRef(String name) throws RemoteException;


    void logout(String name, PrivateChatClientInterface logoutRef) throws RemoteException;

}
