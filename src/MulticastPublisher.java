import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class MulticastPublisher {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buffer;

    public void multicast(String multicastMessage) throws IOException {

        System.out.println("Multicast Publisher Started......");
        System.out.println("MulticastMessage:"+multicastMessage);
        socket = new DatagramSocket();

        group = InetAddress.getByName("230.0.0.0");
        buffer = multicastMessage.getBytes();
        String operation = multicastMessage.split(":")[0].strip();
        File filename = new File(multicastMessage.split(":")[1].strip());

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 4446);
        socket.send(packet);
        socket.close();
    }
}

