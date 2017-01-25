package communication;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import vision.gui.SDPConsole;


/**
 * Created by Simon Rovder
 */

public class SerialPortWrapper implements SerialPortEventListener {

    private SerialPort port = null;
    private SDPPort creator;
    private StringBuilder builder;

    public SerialPortWrapper(SDPPort creator){
        this.creator = creator;
        this.builder = new StringBuilder();
    }

    public void close() throws SerialPortException {
        if(this.port == null) return;
        this.port.closePort();
    }

    public void setSerialPort(SerialPort port){
        if(port == null) return;
        try {
            if(this.port != null) this.port.removeEventListener();
            this.port = port;
            this.port.addEventListener(this);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command){
        if(this.port == null){
            return;
        }
        try {
            SDPConsole.writeln(command);
            this.port.writeBytes((command + "\n").getBytes());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private void nextString(String s){
        this.creator.receivedStringHandler(s);
    }

    private void nextPacket(String s){
        this.nextString(s);
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0){
            try {
                byte buffer[] = this.port.readBytes();
                for (byte b: buffer) {
                    if (b == '\r' || b == '\n') {
                        if(this.builder.length() > 0){
                            String toProcess = this.builder.toString();
                            nextString(toProcess);
                            this.builder.setLength(0);
                        }
                    }
                    else {
                        this.builder.append((char)b);
                    }
                }
            }
            catch (SerialPortException ex) {
                ex.printStackTrace();
                System.out.println("serialEvent");
            }
        }
    }
}
