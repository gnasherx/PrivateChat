package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrivateChatServerInterface extends Remote {
    void login(String name, PrivateChatClientInterface ref) throws RemoteException;

    void sendMessageToServer(byte[] messageByte, PrivateChatClientInterface clientRef) throws RemoteException;

    // TODO: chat.Client will send the name, with which he wants to chat
    // TODO: In the sendClinetRef serch for key of this name and send the clientRef
    PrivateChatClientInterface sendClientRef(String name) throws RemoteException;

    Object[] sendKeysA(PrivateChatClientInterface clientKeyRef) throws RemoteException;

    Object[] sendKeysB(String name) throws RemoteException;


    //void logout(String name, chat.PrivateChatClientInterface logoutRef) throws RemoteException;

}
