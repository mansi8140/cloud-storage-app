import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PublishSubscribe {
    private List<PrintWriter> subscribers = new ArrayList<>();

    public void subscribe(PrintWriter subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(PrintWriter subscriber) {
        subscribers.remove(subscriber);
    }

    public void notifyClients(String message) {
        for (PrintWriter subscriber : subscribers) {
            subscriber.println(message);
        }
    }

}
