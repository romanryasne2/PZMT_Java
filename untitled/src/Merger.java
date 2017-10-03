public class Merger {

    public static int[] Merge(int[] a, int[] b){

        int[] result = new int[a.length + b.length];

        int i = 0;
        int j = 0;

        for (int p = 0; p < result.length; ++p){

            if (j >= b.length || i < a.length && a[i] <= b[j])
            {
                result[p] = a[i];
                ++i;
            }
            else
            {
                result[p] = b[j];
                ++j;
            }
        }

        return result;
    }
}
