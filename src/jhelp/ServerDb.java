package jhelp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class ServerDb implements JHelp {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Connection conn;
    private Statement st;
    private Statement st_param;
    private Set<String> SetColl = new HashSet<String>();

    /**
     * Creates a new instance of <code>ServerDb</code> with default parameters.
     * Default parameters are:<br>
     * <ol>
     * <li><code>ServerDb</code> host is &laquo;localhost&raquo;;</li>
     * <li>{@link java.net.ServerSocket} is opened on
     * {@link jhelp.JHelp#DEFAULT_DATABASE_PORT};</li>
     * </ol>
     */
    public ServerDb() throws IOException {
        this(DEFAULT_DATABASE_PORT);
        System.out.println("ServerDB: default constructor");
    }

    public ServerDb(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("ServerDB socket: " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
        System.out.println("ServerDB: constructor(port)");
    }

    /**
     * Constructor creates new instance of <code>ServerDb</code>.
     *
     * @param args array of {@link java.lang.String} type contains connection
     * parameters.
     */
    public ServerDb(String[] args) {
        System.out.println("ServerDB: constructor(args)");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("SERVERDb: main");
        ServerDb DB = new ServerDb();
        if (DB.connect() == JHelp.READY) {
            System.out.println("OK)");
            DB.run();
        } else {
            System.err.println("Error");
        }
    }

    /**
     * Method defines job cycle for client request processing.
     */
    private void run() throws IOException {
        System.out.println("run");

        clientSocket = serverSocket.accept();

        System.out.println("Local socket: " + clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort());
        System.out.println("Remote socket: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
        input = new ObjectInputStream(clientSocket.getInputStream());
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        while (true) {
            try {
                output.writeObject(getData((Data) input.readObject()));
            } catch (ClassNotFoundException ex) {
                System.err.println("Classnotfound ServerDB " + ex.getMessage());
            }
        }
    }

    /**
     *
     * @return error code. The method returns {@link JHelp#OK} if streams are
     * opened successfully, otherwise the method returns {@link JHelp#ERROR}.
     */
    @Override
    public int connect() {
        String[] args = {"jdbc:derby://localhost:1527/JDB", "root", "1q2w3E"};

        this.connect(args);
        System.out.println("ServerDB: connect");
        return JHelp.READY;
    }

    /**
     * Method sets connection to database and create
     * {@link java.net.ServerSocket} object for waiting of client's connection
     * requests.
     *
     * @return error code. Method returns {@link jhelp.JHelp#READY} in success
     * case. Otherwise method return {@link jhelp.JHelp#ERROR} or error code.
     */
    @Override
    public int connect(String[] args) {
        try {
            conn = DriverManager.getConnection(args[0], args[1], args[2]);
            st = conn.createStatement();
            st_param = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } // собственно коннект
        catch (SQLException ex) {
            System.err.println("ServerDB: Error-> " + ex.getMessage());
            return JHelp.ERROR;
        }
        System.out.println("ServerDB: connected");
        return JHelp.READY;
    }

    /**
     * Method returns result of client request to a database.
     *
     * @param data object of {@link jhelp.Data} type with request to database.
     * @return object of {@link jhelp.Data} type with results of request to a
     * database.
     * @see Data
     * @since 1.0
     */
    @Override
    public Data getData(Data data) {
        System.out.println("SERVERDB: started");
        Item k = data.getKey();
        int state = k.getState();
        String s = k.getItem();
        int kid = k.getId();
        int vid;
        String sql;
        Item[] values;
        ResultSet rs;
        switch (state) {
            case 2:
                System.out.println("SELECT");
                sql = "SELECT Description.Item_description, Items.Item_id FROM Description INNER JOIN Items ON Description.Item_id = Items.Item_id WHERE (Items.Name = '" + s + "')";
                try {
                    rs = st_param.executeQuery(sql);
                    rs.last();
                    int count = rs.getRow();
                    rs.beforeFirst();
                    values = new Item[count];
                    while (rs.next() && rs.getRow() > 0) {
                        Item it = new Item(rs.getInt(2), rs.getString(1), JHelp.ORIGIN);
                        System.out.println("--i= " + rs.getInt(2) + " " + rs.getString(1) + " " + JHelp.ORIGIN);
                        values[rs.getRow() - 1] = it;
                    }
                    Data returndata = new Data();
                    returndata.setValues(values);
                    return returndata;
                } catch (SQLException ex2) {
                    System.err.println("ServerDB: getData(): cant make select query-> " + ex2.getMessage());
                }
                return data;
            case 4:
                System.out.println("INSERT");
                Item v = data.getValue(1);
                String def = v.getItem();
                try {
                    sql = "INSERT INTO Items VALUES ('" + s + "')";
                    st.addBatch(sql);
                    sql = "INSERT INTO Description VALUES ('" + def + "', '" + kid + "')";
                    st.addBatch(sql);
                    st.executeBatch();
                } catch (SQLException ex) {
                    System.err.println("ServerDB: getData(): can`t make insert query-> " + ex.getMessage());
                }
                return data;
            case 8:
                System.out.println("UPDATE");
                values = data.getValues();
                for (Item value : values) {
                    String val = value.getItem();
                    vid = value.getId();
                    sql = "UPDATE Description SET Description.Item_description = '" + val + "' WHERE Descriptions.Item_id = " + vid + "";
                    try {
                        st.addBatch(sql);
                        st.executeBatch();
                    } catch (SQLException ex) {
                        System.err.println("ServerDB: getData(): can`t make update query-> " + ex.getMessage());
                    }
                    return data;
                }
            case 16:
                System.out.println("DELETE");
                try {
                    sql = "DELETE FROM Description WHERE Description.Item_id = " + kid + "";
                    st.addBatch(sql);
                    sql = "DELETE FROM Items WHERE Items.Item_id = " + kid + "";
                    st.addBatch(sql);
                    st.executeBatch();
                } catch (SQLException ex) {
                    System.err.println("ServerDB: getData(): can`t make deleting query-> " + ex.getMessage());
                }
                return data;
            case 32:
                System.out.println("ORIGIN");
                sql = "SELECT Description.Item_description, Items.Item_id FROM Description INNER JOIN Items ON Description.Item_id = Items.Item_id WHERE (Items.Name = '" + s + "')";
                try {
                    rs = st_param.executeQuery(sql);
                    rs.last();
                    int count = rs.getRow();
                    rs.beforeFirst();
                    values = new Item[count];
                    while (rs.next() && rs.getRow() > 0) {
                        Item it = new Item(rs.getInt(2), rs.getString(1), JHelp.ORIGIN);
                        System.out.println("--i= " + rs.getInt(2) + " " + rs.getString(1) + " " + JHelp.ORIGIN);
                        values[rs.getRow() - 1] = it;
                    }
                    Data returndata = new Data();
                    returndata.setValues(values);
                    return returndata;
                } catch (SQLException ex2) {
                    System.err.println("ServerDB: getData(): can`t make select(with Origin parameter) query-> " + ex2.getMessage());
                }
        }
        return data;
    }

    public int disconnect() {
        System.out.println("SERVERDb: disconnect");
        return JHelp.DISCONNECT;
    }

}
