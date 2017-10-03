public class ProgressBar {

    public int Left;

    public int Top;

    public int Height;

    public int Width;

    private int Progress;

    public int GetProgress(){
        return Progress;
    }

    public void SetProgress(int progress){
        if (progress >= 0 && progress <= 100){
            Progress = progress;
        }
    }
}
