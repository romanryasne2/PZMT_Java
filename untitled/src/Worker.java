import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Thread {

    public InetAddress Address;
    public InetAddress LocalAddress;
    private boolean WaitForResponse;
    private int[] MyArray;
    public ArrayList<WorkerListener> WorkerListeners = new ArrayList<WorkerListener>();
    private ServerSocket WorkerSocket;
    private DbLogger Logger;

    public Worker(InetAddress address, InetAddress local){

        Address = address;
        LocalAddress = local;
        WaitForResponse = true;
        Logger = new DbLogger();
    }

    public void DoWork(){

        this.start();
    }

    public void run(){

        WorkerSocket = null;

        try {
            WorkerSocket = new ServerSocket(2727);
        } catch (Exception e) {

        }

        while (WaitForResponse) {

            Socket socket = null;
            WorkMessage work = null;

            try {
                socket = WorkerSocket.accept();

                work = new Gson().fromJson(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine(), WorkMessage.class);

                Logger.IntermediateLog(LocalAddress, "Task is gotten from " + socket.getInetAddress().toString() + ".", work.Array.length);

                socket.close();
            } catch (Exception e) {
                continue;
            }

            if (!work.IsSorted){

                MyArray = work.Array;
                Sort();
            }
            else if (work.Array.length == 0){

                SendToMergeClient(work.Address);
            }
            else{

                Merge(work.Array);
            }


        }
    }

    private void Sort(){

        Sorter sorter = new Sorter(MyArray);

        sorter.SortProgressListeners.add(new SortProgressListener() {

            public void ProgressChanged(int progress) {

                new Thread(() -> {
                    ResultMessage resultMessage = new ResultMessage();

                    resultMessage.Array = new int[0];
                    resultMessage.Progress = progress;

                    try {
                        Socket socket = new Socket(Address, 2727);
                        socket.getOutputStream().write(new Gson().toJson(resultMessage).getBytes());

                        socket.close();
                    } catch (Exception e) {
                    }
                }).start();
            }
        });

        sorter.Sort();
    }

    private void SendReadyMerge(){

        new Thread(() -> {
            ResultMessage resultMessage = new ResultMessage();

            resultMessage.Array = new int[0];
            resultMessage.Progress = 100;

            try {
                byte[] data = new Gson().toJson(resultMessage).getBytes();
                Socket socket = new Socket(Address, 2727);
                socket.getOutputStream().write(data, 0, data.length);
                socket.close();
            } catch (Exception e) {

            }
        }).start();
    }

    private void Merge(int[] array){

        MyArray = Merger.Merge(MyArray, array);
        SendReadyMerge();
    }

    private void SendToMergeClient(InetAddress address){
        new Thread(() -> {
            WorkMessage workMessage = new WorkMessage();

            workMessage.Array = MyArray;
            workMessage.IsSorted = true;

            try {
                byte[] data = new Gson().toJson(workMessage).getBytes();
                Socket socket = new Socket(address.isLoopbackAddress() ? Address : address, 2727);
                socket.getOutputStream().write(data, 0, data.length);

                Logger.IntermediateLog(LocalAddress, "Result is sent to " + socket.getInetAddress().toString() + ".", workMessage.Array.length);

                socket.close();

            } catch (Exception e) {
                return;
            }

            WaitForResponse = false;

            OnFinish();
        }).start();
    }

    private void OnFinish(){

        try {
            WorkerSocket.close();
        }
        catch(Exception e){
            
        }

        for (WorkerListener listener : WorkerListeners){

            listener.OnFinish();
        }
    }
}
