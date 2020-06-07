import org.junit.jupiter.api.Test;

import java.util.Arrays;

class TopKHelperTest {
    @Test
    void topK() {
        TopKHelper topk = new TopKHelper();
        double[] array = {5.5,1.1,2.2,9.9,8.8,3.3,7.7};
        topk.topK(array, 5);
        System.out.println(Arrays.toString(topk.indices));
        System.out.println(Arrays.toString(topk.values));
    }
}