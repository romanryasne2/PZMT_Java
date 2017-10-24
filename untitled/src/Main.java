import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Applet implements ActionListener {

    private int[] Array;
    private ArrayList<Station> Stations;
    private boolean IsBusy;

    private SearchListener searchListener;

    private JSpinner spinner;
    private Button Button;
    private boolean Repaint;

    public void init() {

        Label spinnerLabel = new Label("Count of numbers to sort");

        spinner = new JSpinner(new SpinnerNumberModel(10000000, 100, 1000000000, 100000));

        Button = new Button();
        Button.setLabel("Generate and sort");
        Button.addActionListener(this);

        this.add(spinnerLabel);
        this.add(spinner);
        this.add(Button);

        searchListener = new SearchListener(2727);
        searchListener.onFoundListeners.add((address, port) -> EstablishConnection(address, port));

        Stations = new ArrayList<Station>();
    }

    public void paint(Graphics g) {

        spinner.repaint();

        for (int i = 0; i < Stations.size(); ++i){

            Station station = Stations.get(i);

            g.setColor(Color.BLACK);
            g.drawString(station.Address.toString(), 10, 90 + 40 * i);

            ProgressBarDrawer.Draw(g, Stations.get(i).ProgressBar);
        }
    }

    public void actionPerformed(ActionEvent e) {

        Button.setEnabled(false);

        Stations.clear();
        repaint();

        new Thread(() -> {
            Array = ArrayGenerator.Generate((int) spinner.getValue());
            Stations = StationFinder.FindAll();

            for (int i = 0; i < Stations.size(); ++i) {

                ProgressBar progressBar = Stations.get(i).ProgressBar;

                progressBar.Left = 10;
                progressBar.Top = 100 + 40 * i;
                progressBar.Width = this.getWidth() - progressBar.Left - 10;
                progressBar.Height = 10;
            }

            Controller controller = new Controller(Array, Stations);

            controller.ControllerListeners.add(new ControllerListener() {

                public void OnFinish(int[] array) {

                    Array = array;
                    Button.setEnabled(true);
                    IsBusy = false;
                }

                public void OnProgressBarChanged() {
                    repaint();
                }
            });

            controller.StartControl();
        }).start();
    }

    private void EstablishConnection(InetAddress address, int port){

        if (IsBusy){
            return;
        }
        ArrayList<InetAddress> local = StationFinder.GetLocalIpAddresses();

        if (local.contains(address)){

            return;
        }

        Socket socket = null;

        try{
            socket = new Socket(address, port);
        }
        catch(Exception e){
            
        }

        IsBusy = true;

        Worker worker = new Worker(socket.getInetAddress(), socket.getLocalAddress());

        worker.WorkerListeners.add(new WorkerListener() {

            public void OnFinish() {

                IsBusy = false;
            }
        });

        try {
            socket.close();
        }
        catch(Exception e){
            
        }

        worker.DoWork();
    }
}

