package chat;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;
import java.util.Vector;

public class Server extends UnicastRemoteObject implements PrivateChatServerInterface {

    Vector<ClientInfo> vectorClient;

    private String identity = "chat";

    public Server() throws RemoteException {
        vectorClient = new Vector<>();

        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind(identity, this);
            System.out.println("chat.Server running on port 1099...");
        } catch (MalformedURLException me) {
            System.err.println(me);
        }
    }


    public static void main(String[] args) {
        try {
            new Server();
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    @Override
    public void login(String name, PrivateChatClientInterface ref) {

// TODO: 28-Aug-18 Add RSA public and private keys to this client here

        System.out.println("Generating keys for user : " + name);
        GeneratePublicPrivateKeys generatePublicPrivateKeys = new GeneratePublicPrivateKeys();
        Keys keys = generatePublicPrivateKeys.generateKeys("RSA", 1024);

        PrivateKey privateKey = keys.getPrivateKey();
        PublicKey publicKey = keys.getPublicKey();

        ClientInfo clientInfo = new ClientInfo(name, ref, privateKey, publicKey);

        System.out.println("New chat.Client added");
        vectorClient.add(clientInfo);

        broadcastActiveClientList();
    }

    private void broadcastActiveClientList() {
        Vector<String> vector = new Vector<>();
//        hashMap.forEach((k, v) -> vector.add(k));
//        hashMap.forEach((k, v) -> {
//            try {
//                v.getAllClientList(vector);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        });

        Enumeration<ClientInfo> en = vectorClient.elements();
        while (en.hasMoreElements()) {
            ClientInfo ci = en.nextElement();
            vector.add(ci.name);
        }

        Enumeration<ClientInfo> enumeration = vectorClient.elements();
        while (enumeration.hasMoreElements()) {
            ClientInfo clientInfo = enumeration.nextElement();

            try {
                clientInfo.privateChatClientInterface.getAllClientList(vector);
            } catch (RemoteException remoteException) {
                System.err.println("Error while Brodcasting client list to all clients: " + remoteException);
            }

        }


    }

    @Override
    public void sendMessageToServer(byte[] messageByte, PrivateChatClientInterface clientRef) {
        try {
            clientRef.sendMessageToClient(messageByte);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PrivateChatClientInterface sendClientRef(String name) {
        PrivateChatClientInterface privateChatClientInterface = null;
        Enumeration<ClientInfo> en = vectorClient.elements();
        while (en.hasMoreElements()) {
            ClientInfo ci = en.nextElement();
            if (ci.getName().equals(name)) {
                privateChatClientInterface = ci.getPrivateChatClientInterface();
            }
        }
        return privateChatClientInterface;
    }

    @Override
    public Object[] sendKeysA(PrivateChatClientInterface clientKeyRef) throws RemoteException {
        Object[] keys = new Object[2];
        Enumeration<ClientInfo> en = vectorClient.elements();
        while (en.hasMoreElements()) {
            ClientInfo ci = en.nextElement();
            if (ci.getPrivateChatClientInterface() == clientKeyRef) {
                keys[0] = ci.getPrivateKey();
                keys[1] = ci.getPublicKey();
            }
        }
        return keys;
    }

    @Override
    public Object[] sendKeysB(String name) throws RemoteException {
        Object[] keys = new Object[2];
        Enumeration<ClientInfo> en = vectorClient.elements();
        while (en.hasMoreElements()) {
            ClientInfo ci = en.nextElement();
            if (ci.getName().equals(name)) {
                keys[0] = ci.getPrivateKey();
                keys[1] = ci.getPublicKey();
            }
        }
        return keys;
    }

//    @Override
//    public void logout(String name, chat.PrivateChatClientInterface logoutRef) {
//
//        for (Map.Entry<String, chat.PrivateChatClientInterface> entry : hashMap.entrySet()) {
////                this person(name) is offline now
////                so, tell every online person who is chating with this person(name) that
////                this person(name) is offine now you can not talk with him/her from now.
//
////            entry.getKey is key
////            entry.getValue is value in Hashmap
//            try {
//                entry.getValue().areYouChatingWith(name, logoutRef);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//
//            if (entry.getKey().equals(name)) {
//                hashMap.remove(entry.getKey());
//            }
//        }
//
//        broadcastActiveClientList();
//
//    }
}
