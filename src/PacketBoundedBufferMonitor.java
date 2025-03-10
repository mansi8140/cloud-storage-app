
public class PacketBoundedBufferMonitor {
    private int fullSlots = 0;
    private int capacity = 0;
    private Packet[] buffer=null;
    private int in = 0, out = 0;

    public PacketBoundedBufferMonitor (int capacity) {
        this.capacity = capacity;
        this.buffer=new Packet[capacity];
    }

    public synchronized void deposit(Packet pkt) {
        while (fullSlots == capacity)  {
            try {
                wait();
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.buffer[in] =pkt;
        in = (in + 1) % capacity;
        ++fullSlots;

        notifyAll();
    }

    public synchronized Packet withdraw() {

        Packet pkt;
        while (fullSlots == 0) {
            try {
                wait();
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        pkt = this.buffer[out];
        out = (out + 1) % capacity;
        --fullSlots;

        notifyAll();
        return pkt;
    }

}

