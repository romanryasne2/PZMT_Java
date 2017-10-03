import java.util.Random;

public class ArrayGenerator {

    public static int[] Generate(int count){

        Random random = new Random(System.currentTimeMillis());

        int[] result = new int[count];

        for (int i = 0; i < count; ++i){
            result[i] = Math.abs(random.nextInt());
        }

        return result;
    }
}
