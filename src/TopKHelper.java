import java.util.Arrays;
import java.util.Collections;

public class TopKHelper {
    public double[] values;
    public int[] indices;

    // written by me, but inspired by https://www.geeksforgeeks.org/k-largestor-smallest-elements-in-an-array/
    // and https://stackoverflow.com/questions/18895915/how-to-sort-an-array-of-objects-in-java
    public void topK(double[] array, int k) {
        topK(array, k, false);
    }

    public void topK(double[] array, int k, boolean reverse) {
        if (k > array.length) {
            throw new ArrayIndexOutOfBoundsException("You asked for top " + k + " numbers in an array of size "
                    + array.length);
        }
        IndexedNumber[] inums = new IndexedNumber[array.length];
        for (int i = 0; i < array.length; i++) {
            inums[i] = new IndexedNumber(i, array[i]);
        }
        if (!reverse) {
            Arrays.sort(inums, Collections.reverseOrder());
        } else {
            Arrays.sort(inums);
        }
        double[] values = new double[k];
        int[] indices = new int[k];
        for (int i = 0; i < k; i++) {
            values[i] = inums[i].value;
            indices[i] = inums[i].index;
        }
        this.values = values;
        this.indices = indices;
    }

    public int placement(double[] array, int index){
        IndexedNumber[] inums = new IndexedNumber[array.length];
        for (int i = 0; i < array.length; i++) {
            inums[i] = new IndexedNumber(i, array[i]);
        }
        Arrays.sort(inums, Collections.reverseOrder());
        for (int i = 0; i < inums.length; i++) {
            if (inums[i].index==index){
                return i;
            }
        }
        return -1;
    }
}
class IndexedNumber implements Comparable<IndexedNumber> {
    public int index;
    public double value;

    public IndexedNumber(int index, double value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int compareTo(IndexedNumber o) {
        int comp= new Double(value).compareTo(new Double(o.value));
        return comp;
    }
}