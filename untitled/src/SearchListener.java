import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class SearchListener extends Thread{

    public int Port;

    private DatagramSocket socket;

    public ArrayList<OnFoundListener> onFoundListeners = new ArrayList<OnFoundListener>();

    public SearchListener(int port){

        Port = port;

        try {
            socket = new DatagramSocket(Port);
        }
        catch (Exception e){

        }

        this.start();
    }
        
    private void OnFound(InetAddress address, int port){

        for (OnFoundListener listener : onFoundListeners){
            
            listener.OnFound(address, port);
        }
    }

    public void run(){

        byte[] data = new byte[1024];

        while (true) {

            DatagramPacket receivePacket = new DatagramPacket(data, data.length);

            try {

                socket.receive(receivePacket);
            }
            catch(Exception e){
                continue;
            }

            InetAddress address = receivePacket.getAddress();

            String message = new String(receivePacket.getData());
            message = message.substring(0, message.indexOf(0));

            Gson gson = new Gson();

            SearchMessage searchMessage = gson.fromJson(message, SearchMessage.class);

            OnFound(address, searchMessage.Port);
        }
    }
}
