import java.net.InetAddress;

public interface OnFoundListener {
    
    void OnFound(InetAddress address, int port);
}
