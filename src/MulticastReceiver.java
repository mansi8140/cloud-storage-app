import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];

    public String operation;
    public File filename;
    public int clientNumber;


    public void run() {
        try {
            System.out.println("Multicast Receiver Started......");
            socket = new MulticastSocket(4446);
            InetAddress group = null;
            group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String received = new String(packet.getData(), 0, packet.getLength());
                this.operation = received.split(":")[0].strip();

                this.filename = new File(received.split(":")[1].strip());

                this.clientNumber = Integer.parseInt(received.split(":")[2].strip());


                if (this.clientNumber == 1) {

                    System.out.println((this.operation));
                    if (this.operation.equals("File_deleted")) {
                        File file = new File(Constants.CLIENT_FILE_ROOT2 + this.filename); // replace with the actual path of the file

                        if (file.exists()) {
                            boolean deleted = file.delete();
                            String response = deleted ? "FILE DELETED" : "FILE NOT FOUND";
                            System.out.println("File is deletion status of client 2 : " + response);
                        }
                    }
                    else if (this.operation.equals("New_File_Uploaded")) {
                        File newFile = new File(Constants.SERVER_FILE_ROOT + this.filename); // replace with the actual path of the file
                        FileInputStream fis = null;
                        FileOutputStream fos = null;

                        // Try block to check for exceptions
                        try {
                            fis = new FileInputStream(Constants.SERVER_FILE_ROOT + this.filename);
                            File copyFile = new File(Constants.CLIENT_FILE_ROOT2 + this.filename);
                            if (copyFile.exists()) {
                                System.out.println("Already Have This file no need to add.");
                            } else {
                                copyFile.createNewFile();
                                fos = new FileOutputStream(Constants.CLIENT_FILE_ROOT2 + this.filename);
                                int c;
                                while ((c = fis.read()) != -1) {
                                    fos.write(c);
                                }
                                System.out.println("copied the file successfully");
                            }
                        } finally {
                            if (fis != null) {
                                fis.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        }
                    }
                    else if (this.operation.equals("File_Modified")) {
                        System.out.println("Modification Brodcasted");
                    }
                }
                if (this.clientNumber == 2) {

                    System.out.println((this.operation));
                    if (this.operation.equals("File_deleted")) {
                        File file = new File(Constants.CLIENT_FILE_ROOT + this.filename); // replace with the actual path of the file

                        if (file.exists()) {
                            boolean deleted = file.delete();
                            String response = deleted ? "FILE DELETED" : "FILE NOT FOUND";
                            System.out.println("File is deletion status of client 1 : " + response);
                        }
                    }
                    else if (this.operation.equals("New_File_Uploaded")) {
                        File newFile = new File(Constants.SERVER_FILE_ROOT + this.filename); // replace with the actual path of the file
                        FileInputStream fis = null;
                        FileOutputStream fos = null;

                        // Try block to check for exceptions
                        try {
                            fis = new FileInputStream(Constants.SERVER_FILE_ROOT + this.filename);
                            File copyFile = new File(Constants.CLIENT_FILE_ROOT + this.filename);
                            if (copyFile.exists()) {
                                System.out.println("Already Have This file no need to add.");
                            } else {
                                copyFile.createNewFile();
                                fos = new FileOutputStream(Constants.CLIENT_FILE_ROOT + this.filename);
                                int c;
                                while ((c = fis.read()) != -1) {
                                    fos.write(c);
                                }
                                System.out.println("copied the file successfully");
                            }
                        } finally {
                            if (fis != null) {
                                fis.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        }
                    }
                    else if (this.operation.equals("File_Modified")) {
                        System.out.println("Modification Brodcasted");
                    }
                }

                if ("OK".equals(received)) {
                    break;
                }
            }

            socket.leaveGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socket.close();
    }

    public String getOpt() {
        System.out.println(this.operation + " Function");
        return this.operation;
    }

    public void setOpt(String opt) {
        this.operation = opt;
    }

    public File getFl() {
        System.out.println(this.filename + " File Name");
        return this.filename;
    }

    public void setFl(File fl) {
        this.filename = fl;
    }
}
