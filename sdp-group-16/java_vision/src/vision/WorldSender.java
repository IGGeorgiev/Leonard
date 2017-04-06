package vision;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * Created by s1445541 on 12/03/17.
 */
public class WorldSender{
    final private static int PORT = 5000; // arbitrarily assigned port - same as server

    public static void main(String[] args) throws IOException {
//        System.out.println("wtf 222222");
//        System.out.println(args[0] + " " + args[1] + " " + args[2] + " " + args[3]);
        DatagramSocket socket = new DatagramSocket(); // open new socket

//        String host = "localhost";//"86.0.164.207";
        byte message[] = new byte[1024]; // empty message
        String msgString = "garbage";
        if(args[0] == "BALL"){
            msgString = args[0] + "^" + args[1] + "&" + args[2];
        }
        else if (args[0] == "FRIEND_1" || args[0] == "FRIEND_2" || args[0] == "FOE_1" || args[0] == "FOE_2"){
            msgString = args[0] + "^" + args[1] + "&" + args[2] + "&" + args[3];
        }
        else if (args[0] == "DEFEND" || args[0] == "SHUNT" || args[0] == "SAFE" || args[0] == "GOAL"){
            String mode = "STRATEGY_MODE";
            msgString = mode + "^" + args[0];
            System.out.println("Sender -> " + msgString);
        }
//        System.out.println("Sender -> " + msgString);
        message = msgString.getBytes(); // put String in buffer


        InetAddress address = InetAddress.getByName("localhost"); // determines address
//        System.out.println("Sending to: " + address); // tells user it's doing something
        DatagramPacket packet = new DatagramPacket(message, message.length, address, PORT); // create packet to send

        socket.send(packet); // send packet
//        System.out.println("Message Sent");
//        System.out.println("wtf 3333");
        socket.close();

    }
}