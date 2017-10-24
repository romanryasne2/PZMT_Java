import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    private ArrayList<Station> Stations;
    private int[] Array;
    private boolean WaitForResponse;
    public ArrayList<ControllerListener> ControllerListeners = new ArrayList<ControllerListener>();
    private ServerSocket WorkerSocket;
    private DbLogger Logger;

    private int[] MyArray;

    public Controller(int[] array, ArrayList<Station> stations){

        Array = array;
        Stations = new ArrayList<Station>(stations);
        WaitForResponse = true;
        Logger = new DbLogger();
    }

    public void StartControl(){

        ReceiveResults();
        SendWork();
    }

    private void SendWork(){
        new Thread(){

            public void run(){

                Logger.Log(Stations.get(0).Address, "Task is gotten.", Array.length);

                int interval = Array.length/Stations.size();

                int start = Array.length - interval;

                for (int i = 1; i < Stations.size(); ++i){

                    WorkMessage workMessage = new WorkMessage();
                    workMessage.Array = Arrays.copyOfRange(Array, start, start + interval);
                    workMessage.IsSorted = false;

                    start -= interval;

                    try {
                        Socket socket = new Socket(Stations.get(i).Address, 2727);
                        byte[] data = new Gson().toJson(workMessage).getBytes();
                        socket.getOutputStream().write(data, 0, data.length);

                        Logger.IntermediateLog(Stations.get(0).Address, "Part of sorting task is sent to " + socket.getInetAddress().toString() + ".", workMessage.Array.length);

                        socket.close();
                    }
                    catch (Exception e){

                    }
                }

                MyArray = Arrays.copyOfRange(Array, start, interval);

                Sorter sorter = new Sorter(MyArray);

                sorter.SortProgressListeners.add(new SortProgressListener() {

                    public void ProgressChanged(int progress) {
                        
                        Stations.get(0).ProgressBar.SetProgress(progress);

                        if (progress == 100){
                            ++Stations.get(0).ReadyCount;
                            CheckFinish();
                        }

                        OnProgressBarChanged();
                    }
                });

                sorter.Sort();
            }
        }.start();
    }

    private void ReceiveResults(){

        new Thread() {

            public void run() {
                WorkerSocket = null;

                try {
                    WorkerSocket = new ServerSocket(2727);

                } catch (Exception e) {
                }

                while (WaitForResponse) {

                    Socket socket = null;
                    ResultMessage result = null;

                    try {
                        socket = WorkerSocket.accept();

                        result = new Gson().fromJson(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine(), ResultMessage.class);

                    } catch (Exception e) {
                        continue;
                    }


                    Station station = null;

                    for (Station item : Stations) {

                        if (item.Address.equals(socket.getInetAddress())) {

                            station = item;
                            break;
                        }
                    }

                    if (result.Array.length == 0) {

                        if (station != null) {

                            if (result.Progress > station.ProgressBar.GetProgress()) {
                                station.ProgressBar.SetProgress(result.Progress);
                            }

                            if (result.Progress == 100) {
                                SendMergeClient(station);
                            }

                            OnProgressBarChanged();
                        }
                    } else {

                        Logger.IntermediateLog(Stations.get(0).Address, "Result of part of sorting task is received from " + socket.getInetAddress().toString() + ".", result.Array.length);

                        Merge(result.Array);
                    }

                    try {
                        socket.close();
                    }
                    catch(Exception e){

                    }
                }
            }
        }.start();
    }

    private void Merge(int[] array){

        MyArray = Merger.Merge(MyArray, array);

        CheckFinish();
    }

    private void CheckFinish(){
        if (Stations.size() == 1){

            WaitForResponse = false;
            try {
                WorkerSocket.close();
            }
            catch(Exception e){

            }
            
            Logger.Log(Stations.get(0).Address, "Task is completed.", MyArray.length);

            OnFinish();
        }
    }

    private void SendMergeClient(Station s){
        new Thread() {

            public void run() {

                Station station = Stations.get(Stations.indexOf(s));

                ++station.ReadyCount;

                Station mergeClient = null;

                for (Station item : Stations) {

                    if (station.ReadyCount == item.ReadyCount) {

                        mergeClient = item;
                        break;
                    }
                }

                if (Stations.size() == 2){

                    mergeClient = Stations.get(0);
                }

                if (mergeClient == null){

                    return;
                }

                WorkMessage workMessage = new WorkMessage();
                workMessage.Array = new int[0];
                workMessage.Address = mergeClient.Address;
                workMessage.IsSorted = true;

                try {
                    Socket socket = new Socket(station.Address, 2727);
                    byte[] data = new Gson().toJson(workMessage).getBytes();
                    socket.getOutputStream().write(data, 0, data.length);

                    Logger.IntermediateLog(Stations.get(0).Address, "Worker " + socket.getInetAddress().toString() + " must send result to " + workMessage.Address.toString() + ".", -1);

                    socket.close();
                } catch (Exception e) {
                    return;
                }

                Stations.remove(station);
            }
        }.start();
    }

    private void OnProgressBarChanged(){
        for (ControllerListener listener : ControllerListeners){

            listener.OnProgressBarChanged();
        }
    }

    private void OnFinish(){
        for (ControllerListener listener : ControllerListeners){

            listener.OnFinish(MyArray);
        }
    }
}
