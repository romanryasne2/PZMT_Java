import java.awt.*;

public class ProgressBarDrawer {

    public static void Draw(Graphics graphics, ProgressBar progressBar){
        graphics.setColor(Color.GREEN);

        graphics.fillRect(progressBar.Left, progressBar.Top, (int)(progressBar.Width/100.0*progressBar.GetProgress()), progressBar.Height);
    }
}
