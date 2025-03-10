import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server{
    private static List<Socket> clients = new ArrayList<>();

    public Server() {
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_TCP_PORT)) {

            System.out.println("Server is running on " + Constants.SERVER_TCP_PORT + " tcp port.");
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            System.out.println("\r\n>> Ready to accept requests");
            int uniqueClientId = 1;
            do {
                try {
                    // Wait for clients...
                    Socket client = serverSocket.accept();
                    System.out.println("\n>> New request is accepted with i." + uniqueClientId + Constants.CRLF);

                    // add a new client to the list of connected clients
                    clients.add(client);
                    System.out.println("List of all clients: " + clients.toString());
                    MulticastPublisher multicastPublisher = new MulticastPublisher();
                    executorService.execute(new ClientHandler(client, uniqueClientId, clients,multicastPublisher));


                } catch (IOException io) {
                    System.out.println(">> Fail to listen to requests!");
                    System.exit(1);
                }
                uniqueClientId += 1;
            } while (true);// end of while loop


        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            System.out.println("\n>> Unable to set up port!");
            System.exit(1);
        }

    }
}


class ClientHandler implements Runnable {
    private final Socket client;
    MulticastPublisher multicastPublisher;
    private final
    int uniqueClientId;
    List<Socket> clients;
    private boolean deleted = false;
    private WatchKey watchKey;
    static File newFileCreated;

    public ClientHandler(Socket client, int uniqueClientId, List<Socket> clients,MulticastPublisher multicastPublisher) {
        this.client = client;
        this.uniqueClientId = uniqueClientId;
        this.clients = clients;
        this.multicastPublisher = multicastPublisher;
    }

    @Override
    public void run() {
        try {
            Scanner inputSocket = new Scanner(client.getInputStream());
            PrintWriter outputSocket = new PrintWriter(client.getOutputStream(), true);

            while (true) {
                String line = inputSocket.nextLine();
                int clientUDPPort = 0;

                int Available_SERVER_UDP_PORT = 0;
                String actionType = "";
                if (line.isEmpty()) {
                    line = inputSocket.nextLine();
                }
                if (line.equals("WANT SEND FILES")) {

                    DatagramSocket ServerSocketUDP = null;
                    for (Available_SERVER_UDP_PORT = 16660; Available_SERVER_UDP_PORT <= Constants.SERVER_UDP_PORT_MAX; Available_SERVER_UDP_PORT++) {
                        try {
                            ServerSocketUDP = new DatagramSocket(Available_SERVER_UDP_PORT);
                            System.out.println("Port " + Available_SERVER_UDP_PORT + " is available.");
                            ServerSocketUDP.close();
                            String response = "UDPPORT: SEND REQUEST OK: receive data with the port:" + Available_SERVER_UDP_PORT;
                            System.out.println(">> Response: " + response + Constants.CRLF);

                            outputSocket.println(uniqueClientId + ":" + response + Constants.CRLF);
                            System.out.println(uniqueClientId + ":" + response + Constants.CRLF);
                            break;

                        } catch (IOException e) {
                            if (e.getMessage().contains("Address already in use")) {
                                System.out.println("Port " + Available_SERVER_UDP_PORT + " is already in use.");
                            }
                        }
                    }
                    while (!line.equals("STOP")) {

                        if (line.isEmpty()) {
                            line = inputSocket.nextLine();
                            continue;
                        }
                        if (line.startsWith("SENDING FILE")) {
                            System.out.println(">> Request: " + line + Constants.CRLF);
                            actionType = "SEND REQUEST";
                            clientUDPPort = Integer.parseInt(line.split("#")[2].strip());
                            newFileCreated = new File(line.split("#")[1].strip());
                            break;
                        }
                        line = inputSocket.nextLine();
                    }

                    PacketBoundedBufferMonitor bm = new PacketBoundedBufferMonitor(Constants.MONITOR_BUFFER_SIZE);
                    InetAddress senderIp = client.getInetAddress();// get the IP of the sender
                    InetAddress receiverIp = InetAddress.getByName("localhost");

                    PacketReceiver packetReceiver = new PacketReceiver(bm, receiverIp, Available_SERVER_UDP_PORT, senderIp, clientUDPPort, uniqueClientId);
                    packetReceiver.start();
//
                    FileWriter fileWriter = new FileWriter(bm);
                    fileWriter.start();
                    try {
                        packetReceiver.join();
                        fileWriter.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    multicastPublisher.multicast("New_File_Uploaded:"+newFileCreated +":"+uniqueClientId);

                }
                else if (line.equals("FILE_DELETED")) {
                    outputSocket.println("FILE_DELETED Response: Provide file name to deleted.");
                    line = inputSocket.nextLine();
                    String[] items = line.split(":");
                    System.out.println(items[1]);
                    File file = new File(Constants.SERVER_FILE_ROOT + items[1]); // replace with the actual path of the file

                    boolean deleted = file.delete();
                    String response = deleted ? "FILE DELETED" : "FILE NOT FOUND";
                    System.out.println(">> Response: " + response + Constants.CRLF);
                    outputSocket.println(">> Response: " + response);

                    multicastPublisher.multicast("File_deleted:"+items[1]+":"+uniqueClientId);

                }
                else if(line.equals("FILE_MODIFIED")){
                    outputSocket.println("FILE_MODIFIED Response: Provide file name to Modified.");
                    line = inputSocket.nextLine();
                    System.out.println(line);
                    String[] items = line.split(":");
                    System.out.println(items[1]);
                    File originaFile = new File(Constants.SERVER_FILE_ROOT + items[1]); // replace with the actual path of the file
                    String checkSumClient = items[2];
                    byte[] serverData = Files.readAllBytes(Path.of(Constants.SERVER_FILE_ROOT + items[1]));
                    byte[] serverHash = MessageDigest.getInstance("MD5").digest(serverData);
                    String checksumServer = new BigInteger(1, serverHash).toString(16);
                    System.out.println(checksumServer);
                    if(checksumServer.equals(checkSumClient)){
                        System.out.println("No need to modification");
                        outputSocket.println("NO_MODIFICATION_NEEDED");
                    }else{
                        System.out.println("Needed modification");
                        outputSocket.println("MODIFICATION_NEEDED");
                        outputSocket.println(checksumServer +":"+ originaFile);

                        line = inputSocket.nextLine();
                        String[] items2 = line.split(":");

                        byte[] receivedModifiedBlock = items2[1].getBytes();


                        int receivedStart = Integer.parseInt(items2[2]) ;
                        System.out.println("Start: " + receivedStart) ;


                        // Open the original file in a RandomAccessFile object
                        RandomAccessFile originalFile = new RandomAccessFile(originaFile, "rw");
                        // Convert the byte array to a String
                        System.out.println("Modified Block: "+ Arrays.toString(receivedModifiedBlock));


                        // Seek to the start position where the modified block should be written
                        originalFile.seek(receivedStart);

                        // Write the modified data to the original file
                        originalFile.write(receivedModifiedBlock, 0, receivedModifiedBlock.length);

                        // Write the modified data to the original file

                        // Close the RandomAccessFile object
                        originalFile.close();
                        outputSocket.println("Modification at server is completed ");
                        System.out.println("Modification at server is completed ");

                        multicastPublisher.multicast("File_Modified:"+Constants.SERVER_FILE_ROOT + items[1] +":"+uniqueClientId);
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            deleted = true;
            try {
                clients.remove(new PrintWriter(client.getOutputStream()));
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}





