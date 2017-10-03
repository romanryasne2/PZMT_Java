import java.net.InetAddress;

public class Station {

    public ProgressBar ProgressBar;

    public InetAddress Address;

    public int ReadyCount;

    public Station(InetAddress address){

        Address = address;
        ProgressBar = new ProgressBar();
    }
}
