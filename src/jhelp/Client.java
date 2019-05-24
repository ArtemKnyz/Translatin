package jhelp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements JHelp, ActionListener, WindowListener 
{
    public static final long serialVersionUID = 1234;
    private Properties prop;
    private Data data;
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    JTextField termText = new JTextField("Insert ");
    JTextArea termDefText = new JTextArea("Definitions", 10, 4);
    
    public Client(String[] args) 
    {
        this();
        System.out.println("Client: constructor ");
    }
    public Client() 
    {
        JPanel mPan = new JPanel();
        JPanel mainPan = new JPanel();
        JPanel settingPan = new JPanel();
        JPanel helpPan = new JPanel(); 
        mainPan.setLayout(new BorderLayout());
        settingPan.setLayout(new BorderLayout());
        helpPan.setLayout(new BorderLayout());
        JTabbedPane tabbed = new JTabbedPane();
        tabbed.setSize(600, 360);
        tabbed.setLocation(0, 0);

        tabbed.addTab("Main", mainPan);
        tabbed.addTab("Settings", settingPan);
        tabbed.addTab("Help", helpPan);
        
        JLabel term = new JLabel("Term: ");
        JLabel def = new JLabel("Definitions: ");
        
        
        JButton findBut = new JButton("Find");
        JButton addBut = new JButton("Add");
        JButton editBut = new JButton("Edit");
        JButton delBut = new JButton("Delete");
        JButton nextBut = new JButton("Next");
        JButton prevBut = new JButton("Previous");
        JButton exitBut = new JButton("Exit");
       
        JMenuBar mb = new JMenuBar();
        JMenu fileMB = new JMenu();
        JMenuItem openFileMB = new JMenuItem();
        fileMB.add(openFileMB);
        
        mb.add(fileMB);
        
        mb.add(new JMenu("File"));
        mb.add(new JMenu("Edit"));
        mb.add(new JMenu("Settings"));
        mb.add(new JMenu("Help"));
        
        setJMenuBar(mb);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  
        addWindowListener(this);
        setSize(600,360);
        setLocationRelativeTo(null);

        mPan.setLayout(null);
        mainPan.setLayout(null);

        add(mPan);
        mPan.add(tabbed);
        
        term.setSize(40, 25);
        term.setLocation(8,8);
        termText.setSize(250, 20);
        termText.setLocation(term.getX() + term.getWidth() + 5, 15);
        def.setSize(70, 20);
        def.setLocation(10, term.getY() + term.getHeight()+ 15);
        termDefText.setSize(350, 390);
        termDefText.setLocation(10, def.getY() + def.getHeight()+ 15);
        findBut.setSize(100, 20);
        findBut.setLocation(termDefText.getX()+ termDefText.getWidth() + 10, termText.getY());
        addBut.setSize(100, 20);
        addBut.setLocation(findBut.getX(),termDefText.getY());
        editBut.setSize(100, 20);
        editBut.setLocation(findBut.getX(),addBut.getY() + addBut.getHeight() + 10);
        
        delBut.setSize(100, 20);
        delBut.setLocation(findBut.getX(),editBut.getY() + editBut.getHeight() + 10);
        
        nextBut.setSize(100, 20);
        nextBut.setLocation(findBut.getX(),delBut.getY() + delBut.getHeight() + 30);
        
        prevBut.setSize(100, 20);
        prevBut.setLocation(findBut.getX(),nextBut.getY() + nextBut.getHeight() + 10);
        
        exitBut.setSize(100, 20);
        exitBut.setLocation(findBut.getX(),prevBut.getY() + prevBut.getHeight() + 10);
        //exitBut.setLocation(termDefText.getX(),termDefText.getY() + exitBut.getHeight() + 10);
        
        findBut.addActionListener(this);
        exitBut.addActionListener(this);
        
        mainPan.add(term); 
        mainPan.add(termText);
        mainPan.add(def);
        mainPan.add(termDefText);
        mainPan.add(findBut);
        mainPan.add(addBut);
        mainPan.add(editBut);
        mainPan.add(delBut);
        mainPan.add(nextBut);
        mainPan.add(prevBut);
        mainPan.add(exitBut);
        setResizable(false);
        setTitle("JHelp Client");
        setVisible(true);
    }
    static public void main(String[] args) 
    {
      Client client = new Client(args);
    }
    public void run(String terms) 
    {
        System.out.println("Client: run ");
        Item t = new Item(terms);
        Data d = new Data(t);
        getData(d);
        System.out.println("Client: getData executed");
    }
    @Override
    public int connect() 
    {
        String[] args = {};
        return this.connect(args);
    }
    @Override
    public int connect(String[] args) 
    {
        String host = "localhost";
        int port=JHelp.DEFAULT_SERVER_PORT;
        System.out.println("Client: connect");
        try 
        {
            clientSocket = new Socket (host, port);
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("SUCCESSFULLY connected with serverDb!");
            return JHelp.OK;
        } 
        catch (IOException ex) {System.out.println("Client:IO error in connect(args)");}
        return JHelp.ERROR;
     }
    @Override
    public Data getData(Data data) {
        Data test;  
        try 
        {
            output.writeObject(data);
            test = (Data)input.readObject();
            termDefText.setText("");
            for(int i=0; i<test.getValues(). length;i++)
            {
                termDefText.setText(termDefText. getText() + test.getValue(i).getItem() + "\n");
            }
        } 
        catch (ClassNotFoundException ex) {System.err.println("Client : getData Class definition error -> "+ex.getMessage());} 
        catch (IOException ex) {System.err.println("Client : getData IO error -> "+ ex.getMessage());}
        return null;
    }
    @Override
    public int disconnect() 
    {
        System.out.println("Disconnect");
        return JHelp.DISCONNECT;    
    }
    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        String terms=" ";
        switch (ae.getActionCommand())
        {
            case "Find":
                terms = termText.getText();
                System.out.println(termText.getText());
                if (connect() == JHelp.OK) 
                {
                    run(terms);
                    disconnect();
                }
                break;
                
            case "Insert":
                terms = termText.getText();
                System.out.println(termText.getText());
                if (connect() == JHelp.OK) 
                {
                    run(terms);
                    disconnect();
                }
                break;
            case "Delete":
                terms = termText.getText();
                System.out.println(termText.getText());
                if (connect() == JHelp.OK) 
                {
                    run(terms);
                    disconnect();
                }
                break;
            case "Update":
                terms = termText.getText();
                System.out.println(termText.getText());
                if (connect() == JHelp.OK) 
                {
                    run(terms);
                    disconnect();
                }
                break;
            case "Exit":
                disconnect();
                System.exit (0);     
                break;
        }
    }
    @Override
    public void windowOpened(WindowEvent we) 
    {
    }
    @Override
    public void windowClosing(WindowEvent we) 
    {
        disconnect();
        System.exit (0);
        System.out.println("Processing");
    }
    @Override
    public void windowClosed(WindowEvent we) 
    {
        System.out.println("Closed");
    }
    @Override
    public void windowIconified(WindowEvent we) 
    {
    }
    @Override
    public void windowDeiconified(WindowEvent we) 
    {
    }
    @Override
    public void windowActivated(WindowEvent we) 
    {
    }
    @Override
    public void windowDeactivated(WindowEvent we) 
    {
    }
}
