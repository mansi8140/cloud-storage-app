//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.SocketTimeoutException;
//import java.util.List;
//import java.util.Scanner;
//import java.util.concurrent.Semaphore;
//
//
//public class ClientV2 {
//    private static int Available_CLIENT_UDP_PORT;
//    private static int serverPort;
////    private static Scanner inputSocket;
////    private static PrintWriter outputSocket;
//
//    public ClientV2() {
//    }
//
//    public static void main(String[] args) {
//
//        File directory = new File(Constants.CLIENT_FILE_ROOT);
//        Socket tcpSocket;
//        InetAddress serverIp;
//
//        try {
//            serverIp = InetAddress.getByName("localhost");
//            tcpSocket = new Socket(serverIp, Constants.SERVER_TCP_PORT);
//            Scanner inputSocket = new Scanner(tcpSocket.getInputStream());
//            PrintWriter outputSocket = new PrintWriter(tcpSocket.getOutputStream(), true);
//            String requestOfTransfer = "WANT SEND FILES";
//            outputSocket.println(requestOfTransfer);
//
//            String line = inputSocket.nextLine();
//
//            String[] items = line.split(":");
//
//
//            serverPort = Integer.parseInt(items[items.length - 1]);
//            outputSocket.println("GOT_SERVER_UDP");
//            line = inputSocket.nextLine();
//
//            System.out.println(line);
//            System.out.println(serverPort);
//
//
//           while (true) {
//            File[] files = directory.listFiles();
////            int totalClientFile = files.length;
////            outputSocket.println("Total Files:" +totalClientFile );
//
//            for (File file : files) {
//                String fileName = file.getName();
//
//
//                try {
//                    DatagramSocket socket = null;
//                    int Available_CLIENT_UDP_PORT = 0;
//                    for (Available_CLIENT_UDP_PORT = 15570; Available_CLIENT_UDP_PORT <= Constants.CLIENT_UDP_PORT_MAX; Available_CLIENT_UDP_PORT++) {
//                        socket = new DatagramSocket(Available_CLIENT_UDP_PORT);
//                        // If binding is successful, the port is not in use
//                        System.out.println("Port " + Available_CLIENT_UDP_PORT + " is available.");
//                        // Close the socket after checking
//                        socket.close();
//                        String response = "SENDING FILE " + " # " + fileName + " # " + Available_CLIENT_UDP_PORT;
//                        System.out.println(">> Response: " + response + Constants.CRLF);
//                        outputSocket.println(response + Constants.CRLF + "STOP");
//                        line = inputSocket.nextLine();
//                        System.out.println(line);
//
//                        break;
//
//                    }
//                } catch (Exception e) {
//
//                }
//
//                // start sending the file
//                PacketBoundedBufferMonitor bufferMonitor = new PacketBoundedBufferMonitor(Constants.MONITOR_BUFFER_SIZE);
//                InetAddress senderIp = InetAddress.getByName("localhost");
//
//                PacketSender packetSender = new PacketSender(bufferMonitor, senderIp, Available_CLIENT_UDP_PORT, serverIp, serverPort);
//                packetSender.start();
//
//                FileReader fileReader = new FileReader(bufferMonitor, fileName, Constants.CLIENT_FILE_ROOT);
//                fileReader.start();
//
//                try {
//                    packetSender.join();
//                    fileReader.join();
//                } catch (InterruptedException e) {
//                }
//
//            }
//
//            Thread.sleep(100000);
//           }
//
//
//        } catch (Exception e) {
//
//        }
//    }
//}


// Dishant code with watch services...

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;


import static java.lang.Integer.parseInt;

public class Client {
    private static int Available_CLIENT_UDP_PORT;
    private static int serverPort;
    private static int uniqueClientId;
    static String directory;
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Client Number: ");
        int clientNumber = scanner.nextInt();

        if(clientNumber==1){
            directory = Constants.CLIENT_FILE_ROOT;
        }
        else if(clientNumber==2){
            directory = Constants.CLIENT_FILE_ROOT2;
        }

        File fileDirectory = new File(directory);


        Socket tcpSocket = new Socket(InetAddress.getByName("localhost"), Constants.SERVER_TCP_PORT);
        Scanner inputSocket = new Scanner(tcpSocket.getInputStream());
        PrintWriter outputSocket = new PrintWriter(tcpSocket.getOutputStream(), true);
        System.out.println("Connected with Server");
        // Create WatchService to monitor file changes in directory
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path directoryPath = fileDirectory.toPath();
        WatchKey watchKey = directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        String line;

        while (true) {
            try {
                MulticastReceiver multicastReceiver = new MulticastReceiver();
                Thread t=new Thread(multicastReceiver);
                t.start();




                watchKey = watchService.take();

                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {

                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        String fileName = ev.context().toString();
                        outputSocket.println("WANT SEND FILES");
                        line = inputSocket.nextLine();

                        String[] items = line.split(":");
                        for (int p = 0; p < items.length; p++) {
                            System.out.println(p + ":" + items[p]);
                        }
                        uniqueClientId = parseInt(items[0]);
                        System.out.println(items);
                        serverPort = parseInt(items[items.length - 1]);
                        outputSocket.println("GOT_SERVER_UDP");
                        System.out.println(serverPort);

                        try {
                            DatagramSocket socket = null;
                            int Available_CLIENT_UDP_PORT = 0;
                            for (Available_CLIENT_UDP_PORT = 15570; Available_CLIENT_UDP_PORT <= Constants.CLIENT_UDP_PORT_MAX; Available_CLIENT_UDP_PORT++) {
                                socket = new DatagramSocket(Available_CLIENT_UDP_PORT);
                                // If binding is successful, the port is not in use
                                System.out.println("Port " + Available_CLIENT_UDP_PORT + " is available.");
                                // Close the socket after checking
                                socket.close();
                                String response = "SENDING FILE " + " # " + fileName + " # " + Available_CLIENT_UDP_PORT;
                                System.out.println(">> Response: " + response + Constants.CRLF);
                                outputSocket.println(response + Constants.CRLF + "STOP");
                                line = inputSocket.nextLine();
                                System.out.println(line);

                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // start sending the file
                        PacketBoundedBufferMonitor bufferMonitor = new PacketBoundedBufferMonitor(Constants.MONITOR_BUFFER_SIZE);
                        InetAddress senderIp = InetAddress.getByName("localhost");
                        PacketSender packetSender = new PacketSender(bufferMonitor, senderIp, Available_CLIENT_UDP_PORT, InetAddress.getByName("localhost"), serverPort);
                        packetSender.start();
                        FileReader fileReader = new FileReader(bufferMonitor, fileName, directory);
                        fileReader.start();
                        try {
                            packetSender.join();
                            fileReader.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {

                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        String fileName = ev.context().toString();
                        System.out.println("Entry DELETED: " + fileName);
                        outputSocket.println("FILE_DELETED");
                        line = inputSocket.nextLine();
                        System.out.println(line);
                        outputSocket.println("File_name_of_deleted_File" + ":" + fileName);
                        line = inputSocket.nextLine();
                        System.out.println(line);

                    }
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        String fileName = ev.context().toString();
                        System.out.println("Entry MODIFIED: " + fileName);
                        outputSocket.println("FILE_MODIFIED");
                        line = inputSocket.nextLine();
                        System.out.println(line);


                        byte[] data = Files.readAllBytes(Path.of(directory + fileName));
                        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
                        String checksumClient = new BigInteger(1, hash).toString(16);
                        System.out.println(checksumClient);
                        outputSocket.println("File_name_of_modified_File" + ":" + fileName+ ":" +checksumClient +":" +data );
                        line = inputSocket.nextLine();
                        System.out.println(line);


                        if(line.equals("NO_MODIFICATION_NEEDED")){
                            System.out.println("No needed delta sync");
                        }
                        else if(line.equals("MODIFICATION_NEEDED")){
                            line = inputSocket.nextLine();
                            //  Checksum Received from server
                            String[] items = line.split(":");
                            String serverChecksum = items[0];
                            File originalFile = new File(Constants.SERVER_FILE_ROOT + fileName);
                            // Calculate the start and end index of the modified block
                            byte[] originalData = Files.readAllBytes(Path.of(originalFile.getPath()));
                            System.out.println("Original Data: "+Arrays.toString(originalData));
                            byte[] modifiedData = Files.readAllBytes(Path.of(directory + fileName));
                            System.out.println("Modified Data: "+ Arrays.toString(modifiedData));
                            int start = -1, end = -1;
                            for(int i=0;i<originalData.length;i++){
                                if(originalData[i]!=modifiedData[i]){
                                    start = i;
                                    break;
                                }
                            }
                            if(start == -1){
                                System.out.println("Unable to calculate start and end index of the modified block");
                            }
                            else{
                                // Calculate modified part
                                byte[] modifiedBlock = Arrays.copyOfRange(modifiedData, start, modifiedData.length);
                                System.out.println("ModifiedBlock : "+Arrays.toString(modifiedBlock));
                                // send modifiedBlock to server
                                String s = new String(modifiedBlock, StandardCharsets.UTF_8);
                                System.out.println("Modified Changes: "+s);
                                outputSocket.println("Modified Block :" + s +":"+start);
                                System.out.println("Modification has been send to server.");
                                line = inputSocket.nextLine();
                                System.out.println(line);
                            }
                        }
                    }
                }









                watchKey.reset();

//                t.start();
//                MulticastReceiver m = new MulticastReceiver();
//                m.run();

//                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


