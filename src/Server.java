import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Server extends UnicastRemoteObject implements PrivateChatServerInterface {

    Map<String, PrivateChatClientInterface> hashMap;
    private String identity = "chat";

    public Server() throws RemoteException {
        hashMap = new HashMap<>();

        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind(identity, this);
            System.out.println("Server running on port 1099...");
        } catch (MalformedURLException me) {
            System.err.println(me);
        }
    }

//    broadcastActiveClientList();

    public static void main(String[] args) {
        try {
            new Server();
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    @Override
    public void login(String name, PrivateChatClientInterface ref) {
        hashMap.put(name, ref);

        broadcastActiveClientList();

    }

    private void broadcastActiveClientList() {
        Vector<String> vector = new Vector<>();
        hashMap.forEach((k, v) -> vector.add(k));

        hashMap.forEach((k, v) -> {
            try {
                v.getAllClientList(vector);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

//
//        Iterator it = hashMap.entrySet().iterator();
//        while (it.hasNext()) {
//            HashMap.Entry pair = (HashMap.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//        }
    }

    @Override
    public void sendMessageToServer(String message, PrivateChatClientInterface clientRef) {
        try {
            clientRef.sendMessageToClient(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PrivateChatClientInterface sendClientRef(String name) {
        return hashMap.get(name);
    }

    @Override
    public void logout(String name, PrivateChatClientInterface logoutRef) {
//        hashMap.forEach((k, v) -> {
////                this person(name) is offline now
////                so, tell every online person who is chating with this person(name) that
////                this person(name) is offine now you can not talk with him/her from now.
//
//            try {
//                v.areYouChatingWith(name, logoutRef);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//
//            if (k.equals(name)) {
//                hashMap.remove(k);
//            }
//        });

        for (Map.Entry<String, PrivateChatClientInterface> entry : hashMap.entrySet()) {
//                this person(name) is offline now
//                so, tell every online person who is chating with this person(name) that
//                this person(name) is offine now you can not talk with him/her from now.

//            entry.getKey is key
//            entry.getValue is value in Hashmap
            try {
                entry.getValue().areYouChatingWith(name, logoutRef);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (entry.getKey().equals(name)) {
                hashMap.remove(entry.getKey());
            }
        }

        broadcastActiveClientList();

    }
}
