package communication;

import jssc.*;
import vision.gui.SDPConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Created by Simon Rovder
 *
 * This class is the GUI Wrapper around any port connection. Every robot will have one.
 */
public class SDPPort extends JFrame implements PortListener, ActionListener{

    private String portName = null;
    private SerialPortWrapper serialPortWrapper = new SerialPortWrapper(this);

    private LinkedList<PortListener> listeners;


    private JTextArea inbound;
    private JTextArea outbound;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton autoFind;

    private JTextField portNameField;

    private String expectedResponse = null;

    private boolean connecting;

    public SDPPort() {
        super("SDPPort");
        this.setSize(270,100);
        this.setLayout(null);
        this.listeners = new LinkedList<PortListener>();

        Container con = this.getContentPane();

        this.connectButton    = new JButton("Connect");
        this.disconnectButton = new JButton("Disconnect");
        this.autoFind         = new JButton("Autofind");

        this.inbound  = new JTextArea();
        this.outbound = new JTextArea();

        this.portNameField = new JTextField();

        JScrollPane scroll = new JScrollPane();
        scroll.add(this.inbound);

        this.portNameField.setBounds(0,0,256,20);


        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.connectButton.setBounds(0,30,125,20);
        this.autoFind.setBounds(131,30,125,20);

        this.disconnectButton.setBounds(0,60,256,20);

        con.add(this.connectButton);
        this.connectButton.addActionListener(this);
        con.add(this.disconnectButton);
        this.disconnectButton.addActionListener(this);
        con.add(this.autoFind);
        this.autoFind.addActionListener(this);

        con.add(this.portNameField);


        // Unfinished GUI
//        this.setVisible(true);
    }


    /**
     * This method scans all the available ports for the robot. It sends "ping" to every port
     * and if a port returns the expectedResponse, it saves that port as the robot's port.
     *
     * The loop never ends, so you may mess around with the RF stick as much as you like.
     *
     * All further communication goes through this pot.
     *
     * @param expectedPort If you know the name of the port, put it here. If this is null, scans all ports.
     * @param expectedResponse Put whatever your robot responds to "ping" with.
     */
    public void connect(String expectedPort, String expectedResponse){
        this.expectedResponse = expectedResponse;
        this.listeners = new LinkedList<PortListener>();
        String[] portNames = {expectedPort};
        if(expectedPort == null){
            portNames = SerialPortList.getPortNames();
        }
        SerialPort serialPort;
        String response;
        this.connecting = true;
        // This loops forever, until a robot is found.
        do{
            for(String s : portNames){
                serialPort = new SerialPort(s);
                try {
                    SDPConsole.writeln("Investigating port " + s);
                    serialPort.openPort();
                    serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
                    serialPort.readBytes(serialPort.getInputBufferBytesCount());
                    serialPort.writeBytes("ping\n".getBytes());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    response = serialPort.readString();
                    if(response != null){
                        if(response.contains(expectedResponse)){
                            this.serialPortWrapper.setSerialPort(serialPort);
                            this.portName = s;
                        } else {
                            serialPort.closePort();
                        }
                    } else {
                        serialPort.closePort();
                    }
                } catch (SerialPortException e) {}
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("What the shit just happened, the Thread failed to sleep.. :/");
            }
        } while(this.portName == null && this.connecting);
        SDPConsole.writeln("Breaking news: A robot responding with " + expectedResponse + " has been found on the port " + this.portName);
        this.visualiseConnect(ConnectionStates.CONNECTED);
    }

    /**
     * Method safely closes the port.
     */
    public void closePorts(){
        try {
            this.serialPortWrapper.close();
        } catch (Exception e) {
            System.out.println("Port already closed. You must open the port to close it.");
        }
    }

    public boolean isConnected(){
        return this.portName != null;
    }

    /**
     * If you want anything to listen to the incomming stuff from the robot, put it into this method.
     * @param listener Your class
     */
    public void addCommunicationListener(PortListener listener){
        this.listeners.add(listener);
    }

    /**
     * The main method for sending commands.
     * @param command The command
     * @param args Numerical parameters
     */
    public void commandSender(String command, int ... args){
        StringBuilder sb = new StringBuilder();
        sb.append(command);
        if(args != null){
            for(int i : args){
                sb.append(' ');
                sb.append(i);
            }
        }
        String toSend = sb.toString();
        this.outbound.append(toSend);
        this.serialPortWrapper.sendCommand(toSend);
    }

    @Override
    public void receivedStringHandler(String string) {
        this.inbound.append(string);
        for(PortListener listener : this.listeners){
            listener.receivedStringHandler(string);
        }
    }

    private enum ConnectionStates {
        CONNECTED, DISCONNECTED, CONNECTING
    }

    private void visualiseConnect(ConnectionStates state){
        this.connectButton.setEnabled(state == ConnectionStates.DISCONNECTED);
        this.disconnectButton.setEnabled(state == ConnectionStates.CONNECTED || state == ConnectionStates.CONNECTING);
        switch(state){
            case CONNECTED:
                this.disconnectButton.setText("Disconnect");
                break;
            case DISCONNECTED:
                this.disconnectButton.setText("Disconnect");
                break;
            case CONNECTING:
                this.disconnectButton.setText("Connecting... (STOP)");
                break;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(this.expectedResponse == null){
            SDPConsole.writeln("There is no Expected Response in the Communications class.. This is very very bad, pls fix.");
            return;
        }
        String exp = this.expectedResponse;
        if(e.getSource() == this.connectButton){
            this.visualiseConnect(ConnectionStates.CONNECTING);
            (new Thread() {
                public void run() {
                    connect(portNameField.getText(), exp);
                }
            }).start();
        } else if (e.getSource() == this.disconnectButton){
            this.connecting = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            this.visualiseConnect(ConnectionStates.DISCONNECTED);
            this.closePorts();
        } else if (e.getSource() == this.autoFind){
            visualiseConnect(ConnectionStates.CONNECTING);
            (new Thread() {
                public void run() {
                    connect(null, exp);
                }
            }).start();
        }
    }

    public static void main(String [] args){
        (new SDPPort()).connect(null, "pang");
    }
}
