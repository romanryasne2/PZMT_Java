import java.util.ArrayList;

public class Sorter {

    private long Comparisons;
    private long TotalComparisons;
    private int Progress;
    public ArrayList<SortProgressListener> SortProgressListeners = new ArrayList<SortProgressListener>();
    private int[] Array;

    public Sorter(int[] a){

        TotalComparisons = (long)(a.length/Math.log(2) * Math.log(a.length));
        Comparisons = 0;
        Progress = 0;
        Array = a;
    }

    public void Sort(){

        QuickSort(Array, 0, Array.length - 1);
        Progress = 100;
        OnProgressChanged();
    }

    private void QuickSort(int[] a, int start, int end)
    {
        int i = start;
        int j = end;
        double x = a[(start + end)/2];
        while (i < j)
        {
            while (a[i] < x)
            {
                ++i;
                IncrementComparisons();
            }
            while (a[j] > x)
            {
                --j;
                IncrementComparisons();
            }
            if (i <= j)
            {
                int c = a[i];
                a[i] = a[j];
                a[j] = c;
                ++i;
                --j;
            }
        }
        if (start < j)
        {
            QuickSort(a, start, j);
        }
        if (i < end)
        {
            QuickSort(a, i, end);
        }
    }

    private void IncrementComparisons(){

        Comparisons += 100;

        int progress = (int)((double)Comparisons/TotalComparisons);

        if (progress > Progress && progress % 5 == 0){
            Progress = progress;
            OnProgressChanged();
        }
    }

    private void OnProgressChanged(){
        for (SortProgressListener listener : SortProgressListeners){

            listener.ProgressChanged(Progress);
        }
    }
}
