package chat;

import java.security.PrivateKey;
import java.security.PublicKey;

public class ClientInfo {
    PrivateChatClientInterface privateChatClientInterface;
    String name;
    PrivateKey privateKey;
    PublicKey publicKey;

    public ClientInfo() {
    }


    public ClientInfo(String name, PrivateChatClientInterface privateChatClientInterface) {
        this.privateChatClientInterface = privateChatClientInterface;
        this.name = name;
    }

    public ClientInfo(String name, PrivateChatClientInterface privateChatClientInterface, PrivateKey privateKey, PublicKey publicKey) {
        this.privateChatClientInterface = privateChatClientInterface;
        this.name = name;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public PrivateChatClientInterface getPrivateChatClientInterface() {
        return privateChatClientInterface;
    }

    public ClientInfo getRef() {
        return this;
    }

    public String getName() {
        return name;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public boolean equals(Object object) {
        System.out.println("running");
        if (object instanceof ClientInfo) {
            return ((ClientInfo) object).equals(this.name);
        } else {
            return false;
        }
    }


}
