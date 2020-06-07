import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PageRankTest {

    @Test
    void topKPageRank() {
        String dataPath=DataPath.dataPath;
        PageRank pr = new PageRank(dataPath+"/correctGraph.txt", 0.1, 0.85);
        int k = 5;
        int[] topkr = pr.topKPageRank(k);
        System.out.println(Arrays.toString(topkr));
        double[] rank = pr.rank;
        for (int bigval : topkr
        ) {
            int cnt = 0;
            for (double r : rank
            ) {
                if (bigval < r) {
                    cnt++;
                }
                if (cnt==6){
                    assertTrue(cnt != 6);
                }
            }
        }
    }
    
    @Test
    void numEdges() {
    	PageRank pr = new PageRank(DataPath.dataPath+"/correctGraph.txt", 0.01, 0.85);
    	System.out.println("numEdges : " + pr.numEdges());
    }

    @Test
    void main() {
        System.out.println("epsilon=0.01, beta=0.85");
        PageRank pr = new PageRank(DataPath.dataPath+"/correctGraph.txt", 0.01, 0.85);
        System.out.println("Steps:"+pr.stepsTaken);
        System.out.println("epsilon=0.01, beta=0.25");
        pr = new PageRank(DataPath.dataPath+"/WikiSportsGraph.txt", 0.01, 0.25);
        System.out.println("Steps:"+pr.stepsTaken);


        System.out.println("epsilon=0.0001, beta=0.85");
        pr = new PageRank(DataPath.dataPath+"/correctGraph.txt", 0.0001, 0.85);
        System.out.println("Steps:"+pr.stepsTaken);
        System.out.println("epsilon=0.0001, beta=0.25");
        pr = new PageRank(DataPath.dataPath+"/WikiSportsGraph.txt", 0.0001, 0.25);
        System.out.println("Steps:"+pr.stepsTaken);
    }

    @Test
    void trustRank() {
        PageRank pr = new PageRank(DataPath.dataPath+"/correctGraph.txt", 0.01, 0.85);
        double[] ret=pr.trustRank(pr.pageRank());
        System.out.println(Arrays.toString(ret));
        System.out.println("Steps taken for trust rank: "+pr.trustStepsTaken);
    }

    @Test
    void compare(){
        PageRank pr = new PageRank(DataPath.dataPath+"/correctGraph.txt", 0.01, 0.85);
        double[] pageRank=pr.pageRank();
        double[] trustRank=pr.trustRank(pr.pageRank());
        System.out.println(Arrays.toString(pageRank));
        System.out.println(Arrays.toString(trustRank));
        double prsum=0;
        double trsum=0;
        double diff=0;
        for (int i = 0; i < pageRank.length; i++) {
            diff+=Math.abs(trustRank[i]-pageRank[i]);
            trsum+=Math.abs(trustRank[i]);
            prsum+=Math.abs(pageRank[i]);
        }
        System.out.println(diff);
        System.out.println(prsum);
        System.out.println(trsum);

        TopKHelper topk= new TopKHelper();
        topk.topK(trustRank,10);
        System.out.println(Arrays.toString(topk.indices));
        topk.topK(pageRank,10);
        System.out.println(Arrays.toString(topk.indices));

        topk.topK(trustRank,10, true);
        System.out.println(Arrays.toString(topk.indices));
        topk.topK(pageRank,10, true);
        System.out.println(Arrays.toString(topk.indices));
    }

    @Test
    void POP(){
        double beta=0.95;
        PageRank pr = new PageRank(DataPath.dataPath+"/correctGraph.txt", 0.000001, beta);
        int numPages=pr.fromToMatrix.length;
        double[][] o =new double[numPages][numPages];
        for (int i = 0; i < numPages; i++) {
            Arrays.fill(o[i],0);
        }

        for (int i = 0; i <pr.fromToMatrix.length; i++) {
            for (int j = 0; j < pr.fromToMatrix.length; j++) {
                if (pr.totalLinks[i]==0) {
                    // N tilde
                    o[i][j] = 1.0 / numPages;
                }else{
                    if(pr.fromToMatrix[i][j]){
                        // M tilde
                        o[i][j]=1.0/pr.totalLinks[i];
                    }
                }
            }
        }

        // O
        for (int i = 0; i < numPages; i++) {
            for (int j = 0; j < numPages; j++) {
                o[i][j]=beta*o[i][j]+(1-beta)/numPages;
            }
        }

        double [] rank = pr.pageRank();
        double[] matmul=new double[numPages];
        Arrays.fill(matmul,0);
        for (int i = 0; i < pr.fromToMatrix.length; i++) {
            double v = rank[i];
            for (int j = 0; j < pr.fromToMatrix.length; j++) {
                matmul[j]+=o[i][j]*v;
            }
        }
        System.out.println(Arrays.toString(rank));
        System.out.println(Arrays.toString(matmul));
        double diff=0;
        for (int i = 0; i < numPages; i++) {
            diff+=(Math.abs(rank[i]-matmul[i]));
        }
        System.out.println(diff);
    }
    
    
}