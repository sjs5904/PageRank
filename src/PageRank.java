import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class PageRank {
    private final File graphFile;
    private final String graphFileName;
    private final double epsilon;
    private final double beta;
    HashMap<String, Integer> nameNode;
    String[] nodeName;
    // this is M matrix
    boolean[][] fromToMatrix;
    int[] totalLinks;
    private int totalNodes;
    double[] rank;
    double[] trustRank;
    int stepsTaken;
    int trustStepsTaken;



    public PageRank(String graphFileName, double epsilon, double beta) {
        this.graphFileName = graphFileName;
        this.epsilon = epsilon;
        this.beta = beta;
        this.graphFile = new File(graphFileName);
        processGraph(graphFile);
        approxPageRank();
    }

    private void approxPageRank() {
        System.out.println("Calculating page rank. The page rank will be stored in the PageRank object until " +
                "approxPageRank() is called again");
        double[] p = new double[totalNodes];
        Arrays.fill(p, 1.0 / totalNodes);
        boolean converged = false;
        int step=0;
        while (!converged) {
            double[] newP=walk(p);
            if (norm(p, newP)<=epsilon){
                converged=true;
            }
            p=newP;
            step++;
            if (step == 100000) {
                System.out.println("This is too many random walks. I will abort the program");
                System.exit(-1);
            }
        }
        rank=p;
        stepsTaken=step;
    }

    public double norm(double[] p, double[] newP) {
        double normSum=0;
        for (int i = 0; i < p.length; i++) {
            normSum+=Math.abs(p[i]-newP[i]);
        }
        return normSum;
    }

    public double[] walk(double[] oldRank) {
        int n = oldRank.length;
        double[] newRank = new double[n];
        Arrays.fill(newRank, (1-beta)/n);
        for (int p = 0; p < n; p++) {
            if (totalLinks[p]!=0){
                // if not zero
                boolean[] toLinks = fromToMatrix[p];
                double delta =beta*oldRank[p]/totalLinks[p];
                for (int q = 0; q < n; q++) {
                    if (toLinks[q]){
                        newRank[q]+=delta;
                    }
                }
            }else{
                // if zero
                double delta =beta*oldRank[p]/n;
                for (int q = 0; q < n; q++) {
                    newRank[q]+=delta;
                }
            }
        }
        return newRank;
    }

    public void processGraph(File graphFile){
        try {
            Scanner scan;
            scan = new Scanner(graphFile);
            if (scan.hasNextInt()){
                totalNodes=scan.nextInt();
                fromToMatrix = new boolean[totalNodes][totalNodes];
                nodeName = new String[totalNodes];
                totalLinks = new int[totalNodes];
            }else{
                System.out.println("Your file contains no vertices count");
                System.exit(-1);
                return;
            }
            nameNode = new HashMap<>();
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                if (!line.equals("")){
                    String[] words=line.split("\\s");
                    String fromNode=words[0];
                    String toNode=words[1];
                    Integer fromIdx;
                    Integer toIdx;
                    fromIdx= nameNode.get(fromNode);
                    toIdx= nameNode.get(toNode);
                    if (fromIdx==null){
                        fromIdx= nameNode.size();
                        nameNode.put(fromNode,fromIdx);
                        nodeName[fromIdx]=fromNode;
                    }
                    if (toIdx==null){
                        toIdx= nameNode.size();
                        nameNode.put(toNode,toIdx);
                        nodeName[toIdx]=toNode;
                    }
                    if (fromToMatrix[fromIdx][toIdx]==false){
                        fromToMatrix[fromIdx][toIdx]=true;
                    }
                }
            }

            Arrays.fill(totalLinks, 0);
            for (int i = 0; i < fromToMatrix.length; i++) {
                boolean[] toPages=fromToMatrix[i];
                int linksCnt=0;
                for (int j = 0; j < toPages.length; j++) {
                    if (toPages[j]){
                        linksCnt++;
                    }
                }
                totalLinks[i]=linksCnt;
            }
            return;
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file " +graphFile);
            System.out.println("Current path: "+System.getProperty("user.dir"));
            System.exit(-1);
            return;
        }
    }

    void correctGraph(String targetFileName){
        File targetFile = new File(targetFileName);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(targetFile));
        } catch (IOException e) {
            System.out.println("Cannot write");
            System.exit(-2);
        }

        try {
            writer.write(""+totalNodes+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < fromToMatrix.length; i++) {
            boolean[] toPages=fromToMatrix[i];
            for (int j = 0; j <toPages.length; j++) {
                if (toPages[j]){
                    String fromName=""+(i+1);
                    String toName=""+(j+1);
                    try {
                        String line =fromName+ " "+toName+"\n";
                        writer.write(line);
                    } catch (IOException e) {
                        System.out.println("Writer cannot write");
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double pageRankOf(int vertex){
        return rank[nameNode.get(""+vertex)];
    }

    public double[] pageRank(){
        return rank;
    }
    
    int getMinIndex(double[] array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
    
        double min = array[0];
        int index = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                index = i;
            }
        }
    
        return index;
    }

//    double[] trustRank(double[] trust){
//        DoubleInt ValInd=topK(trust, trust.length/4);
//        int[] trustPages = ValInd.indices;
//        double[] tr=trustWalk(rank, trustPages);
//        return tr;
//    }

    public double[] trustRank(double[] trust){
        TopKHelper topk=new TopKHelper();
        topk.topK(trust,trust.length/4);
        int[] trustPages = topk.indices;
//        DoubleInt ValInd=topK(trust, trust.length/4);
//        int[] trustPages = ValInd.indices;

        double[] p = new double[totalNodes];
        Arrays.fill(p, 1.0 / totalNodes);
        boolean converged = false;
        int step=0;
        while (!converged) {
            double[] newP=trustWalk(p, trustPages);
            if (norm(p, newP)<=epsilon){
                converged=true;
            }
            p=newP;
            step++;
            if (step == 100000) {
                System.out.println("This is too many runs. I will abort the program");
                System.exit(-1);
            }
        }
        trustRank=p;
        trustStepsTaken=step;
        return p;
    }


    private double[] trustWalk(double[] oldRank, int[] trustVertices) {
        int n = oldRank.length;
        double[] newRank = new double[n];
        Arrays.fill(newRank, 0);
        for (int node :
                trustVertices) {
            newRank[node] = (1-beta) / trustVertices.length;
        }
        for (int p = 0; p < n; p++) {
            if (totalLinks[p]!=0){
                // if not zero
                boolean[] toLinks = fromToMatrix[p];
                double delta =beta*oldRank[p]/totalLinks[p];
                for (int q = 0; q < n; q++) {
                    if (toLinks[q]){
                        newRank[q]+=delta;
                    }
                }
            }else{
                // if zero
                double delta =beta*oldRank[p]/n;
                for (int q = 0; q < n; q++) {
                    newRank[q]+=delta;
                }
            }
        }
        return newRank;
    }

    public int numEdges(){
        int total=0;
        for (int i = 0; i < totalLinks.length; i++) {
            total+=totalLinks[i];
        }
        return total;
    }


    public int[] topKPageRank(int k){
        TopKHelper topk=new TopKHelper();
        topk.topK(rank,k);
        int[] indices= topk.indices;
        return indicesToNames(indices);
//        int[] names=new int[indices.length];
//        for (int i = 0; i < indices.length; i++) {
//            names[i]= Integer.parseInt(nodeName[indices[i]]);
//        }
//        return names;
    }

    public int[] indicesToNames(int[] indices){
        int[] names=new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            names[i]= Integer.parseInt(nodeName[indices[i]]);
        }
        return names;
    }
}


