import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Vector;

public class Client extends JFrame implements PrivateChatClientInterface, ActionListener {
    Container container;
    JTextArea jTextArea;
    JTextField jTextField;
    JLabel jLabel;
    JButton jButton;
    JList list;
    JScrollPane jsp1, jsp2;
    DefaultListModel model;
    PrivateChatServerInterface ref;
    PrivateChatClientInterface clientRef;
    private String clientName;

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
            System.setProperty("java.rmi.server.hostname", "192.168.0.6");
            ref = (PrivateChatServerInterface) Naming.lookup("rmi://localhost:1099/chat");
            UnicastRemoteObject.exportObject(this);

            ref.login(clientName, this);

        } catch (Exception e) {
            System.err.println(e);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    System.out.println("Inside addWindowCloseListener");
                    ref.logout(clientName, Client.this);

                } catch (RemoteException expression) {
                    System.err.println(expression);
                }

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

    @Override
    public void sendMessageToClient(String message) {
        jTextArea.append(message + "\n");
    }

    @Override
    public void getAllClientList(Vector<String> clients) {

        clients.remove(new String(clientName));

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
        message = clientName + ": " + message;

        try {
            if (!jTextField.getText().isEmpty()) {
                ref.sendMessageToServer(message, clientRef);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

        jTextField.setText("");
    }
}
