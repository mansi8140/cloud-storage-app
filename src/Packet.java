import java.io.FileInputStream;
import java.util.Arrays;

public class Packet {

    private int sequenceNumber;
    private int index=0;
    private byte[] content=new byte[Constants.MAX_DATAGRAM_SIZE];
    private int contentSize=0;


    public Packet() {}
    public Packet(int index) {
        this.index=index;
    }
    public Packet(int index,byte[] content,int readSize) {
        this.index=index;
        this.content=content;
        this.contentSize=readSize;
    }


    public Packet(byte[] packetArray, int length) {
        if (length>=4) {
            this.index=Helper.byteArrayToInt(Helper.get4Bytes(packetArray));
            this.content=Arrays.copyOfRange(packetArray, 4, length);
            this.contentSize=this.content.length;
        }else {
            System.out.println("Error: the packet size should be >= 4.");
        }
    }


    public byte[] packetToByteArray() {
        byte[] packetArray=new byte[this.contentSize+4];
        Helper.save4Bytes(Helper.intToByteArray(this.index), packetArray);
        System.arraycopy(this.content, 0,packetArray, 4, this.contentSize);
        return packetArray;
    }



    public int getIndex() {	return this.index;}
    public void setIndex(int index) {this.index=index;}
    public void setContent(byte[] content) {
        this.content=content;
        this.contentSize=content.length;
    }
    public String getContentInString() {
        return new String(this.content,0,this.contentSize);
    }
    public byte[] getContent() {
        return this.content;
    }
    public int getContentSize() {return this.contentSize;}

}

