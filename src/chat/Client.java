package chat;

import javax.crypto.Cipher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;
import java.util.Vector;

public class Client extends JFrame implements PrivateChatClientInterface, ActionListener {
    private Container container;
    private JTextArea jTextArea;
    private JTextField jTextField;
    private JLabel jLabel;
    private JButton jButton;
    private JList list;
    private JScrollPane jsp1, jsp2;
    private DefaultListModel model;
    private PrivateChatServerInterface ref;
    private PrivateChatClientInterface clientRef;
    private String clientName;
    private Object[] keysA, keysB;
    private PrivateKey privateKeyA, privateKeyB;
    private PublicKey publicKeyA, publicKeyB;

    public Client(String name) {
        super("Chat client: " + name);
        clientName = name;
        setSize(720, 475);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        container = getContentPane();
        container.setLayout(null);

        jLabel = new JLabel();
        jLabel.setBounds(5, 5, 500, 15);
        container.add(jLabel);


        jTextArea = new JTextArea();
        jsp1 = new JScrollPane(jTextArea);
        jsp1.setBounds(5, 25, 500, 380);
        container.add(jsp1);

        model = new DefaultListModel();

        list = new JList(model);
        jsp2 = new JScrollPane(list);
        jsp2.setBounds(510, 25, 200, 380);
        container.add(jsp2);

        jTextField = new JTextField();
        jTextField.setBounds(5, 410, 500, 25);
        container.add(jTextField);

        jButton = new JButton("SEND");
        jButton.setBounds(510, 410, 200, 25);
        container.add(jButton);

        jTextArea.setEditable(false);
        setVisible(true);

        try {
            System.setProperty("java.rmi.server.hostname", "192.168.0.5");
            ref = (PrivateChatServerInterface) Naming.lookup("rmi://localhost:1099/chat");
            UnicastRemoteObject.exportObject(this);

            ref.login(clientName, this);

//            Get the keys from Server
            keysA = ref.sendKeysA(this);
            privateKeyA = (PrivateKey) keysA[0];
            publicKeyA = (PublicKey) keysA[1];


        } catch (Exception e) {
            System.err.println(e);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//                try {
//                    ref.logout(clientName, chat.Client.this);
//
//                } catch (RemoteException expression) {
//                    System.err.println(expression);
//                }

            }
        });

        jButton.addActionListener(this);
        jTextField.addActionListener(this);

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedName = (String) list.getSelectedValue();


                Color green = Color.decode("#329832");
                jLabel.setForeground(green);
                jLabel.setText(selectedName + " is online");
                try {
                    clientRef = ref.sendClientRef(selectedName);
                    keysB = ref.sendKeysB(selectedName);
                    privateKeyB = (PrivateKey) keysB[0];
                    publicKeyB = (PublicKey) keysB[1];

                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        };
        list.addMouseListener(mouseListener);

    }

    public static void main(String[] args) {
        new Client(args[0]);
    }

    public static byte[] encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(message.getBytes());
    }

    public static byte[] decrypt(PrivateKey privateKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(encrypted);
    }

    @Override
    public void sendMessageToClient(byte[] message) {

//        now Decrypting the message
        try {
            byte[] secret = decrypt(privateKeyB, message);
            System.out.println("message========> : " + secret);
        } catch (Exception e) {
            System.err.println("Error while decrypting and showing your message");
            e.printStackTrace();
        }
//        jTextArea.append(se + "\n");
    }

    @Override
    public void getAllClientList(Vector<String> clients) {

        clients.remove(clientName);


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                model.removeAllElements();
                Enumeration<String> en = clients.elements();
                while (en.hasMoreElements()) {
                    String s = en.nextElement();
                    model.addElement(s);
                }
            }
        });
    }

    //    not completly implemented yet.
    @Override
    public void areYouChatingWith(String name, PrivateChatClientInterface offlineClientRef) {

        if (clientRef.equals(offlineClientRef)) {
            jLabel.setForeground(Color.red);
            jLabel.setText(name + " is offline now");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = jTextField.getText();
        byte[] encrypted = new byte[2048]; ///////////////////////////////be Careful here//////////////////////////////
        message = clientName + ": " + message;

//        Now Encrypting this message using private key
        try {
            encrypted = encrypt(publicKeyB, message);
        } catch (Exception e1) {
            System.err.println("Error while encrypting the message");
            e1.printStackTrace();
        }

        try {
            if (!jTextField.getText().isEmpty()) {
                ref.sendMessageToServer(encrypted, clientRef);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

        jTextField.setText("");
    }

}
