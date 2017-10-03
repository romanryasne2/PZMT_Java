import com.google.gson.Gson;

import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationFinder {

    private static boolean WaitForResponse;
    private static ServerSocket Socket;

    private static ArrayList<Station> Result;

    public static ArrayList<Station> FindAll() {

        Result = new ArrayList<Station>();

        Thread searcher = new Thread(){

            public void run(){

                Socket = null;

                ArrayList<InetAddress> local = GetLocalIpAddresses();

                try{
                    Socket = new ServerSocket(2727);
                }
                catch (Exception e){

                }

                while (WaitForResponse){

                    Socket stationSocket = null;

                    try{
                        stationSocket = Socket.accept();
                    }
                    catch (Exception e){
                        continue;
                    }

                    if (!local.contains(stationSocket.getInetAddress())){

                        Result.add(new Station(stationSocket.getInetAddress()));
                    }
                }
            }
        };

        WaitForResponse = true;

        searcher.start();

        ArrayList<InetAddress> addresses = GetBroadcastIpAddresses();

        for (InetAddress address : addresses) {

            byte[] data = new Gson().toJson(new SearchMessage(2727)).getBytes();

            DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, 2727);

            try {
                new DatagramSocket().send(sendPacket);

            } catch (Exception e) {

            }
        }

        try {
            Thread.sleep(5000);

        }
        catch (Exception e) {

        }

        WaitForResponse = false;

        try{
            Socket.close();
        }
        catch (Exception e){
        }

        Result.add(new Station(InetAddress.getLoopbackAddress()));
        int index = 0;

        for (int i = 0; i < Result.size(); ++i){
            if (Result.get(i).Address == InetAddress.getLoopbackAddress()){
                index = i;
                break;
            }
        }

        if (index != 0){
            Station temp = Result.get(0);
            Result.set(0, Result.get(index));
            Result.set(index, temp);
        }

        return Result;
    }

    private static ArrayList<InetAddress> GetBroadcastIpAddresses() {

        ArrayList<NetworkInterface> nics = null;

        try {

            nics = Collections.list(NetworkInterface.getNetworkInterfaces());

        } catch (Exception e) {

        }

        ArrayList<InetAddress> result = new ArrayList<InetAddress>();

        for (int i = 0; i < nics.size(); ++i){

            List<InterfaceAddress> addresses = nics.get(i).getInterfaceAddresses();

            for (int j = 0; j < addresses.size(); ++j) {

                InetAddress address = addresses.get(j).getBroadcast();
                if (address != null)  {
                    result.add(address);
                }
            }
        }

        return result;
    }

    public static ArrayList<InetAddress> GetLocalIpAddresses() {

        ArrayList<NetworkInterface> nics = null;

        try {

            nics = Collections.list(NetworkInterface.getNetworkInterfaces());

        } catch (Exception e) {

        }

        ArrayList<InetAddress> result = new ArrayList<InetAddress>();

        for (int i = 0; i < nics.size(); ++i){

            List<InetAddress> addresses = Collections.list(nics.get(i).getInetAddresses());

            for (int j = 0; j < addresses.size(); ++j) {

                result.add(addresses.get(j));
            }
        }

        return result;
    }
}
