import java.io.File;
import java.util.*;

public class PositionalIndex {

    PreProcessing pre;
    File folder;
    int numDoc;
    String folderName;
    String[] docs;
    //	List<String> allUniqueTerms;
    //	HashMap<String, TDFT> dict;
    // Inverted Index, term -> postings. A posting is all the indices of the term in
    // a particular document
    HashMap<String, Posting[]> termPostings;
    HashMap<String, Integer> uniqueWordIndex;

    public PositionalIndex(String folderName) {
//        long start = System.currentTimeMillis();

        this.folderName = folderName;
        folder = new File(this.folderName);
        try{
            numDoc = folder.list().length;
        }catch (NullPointerException e){
            System.out.println("You do not have files in the specified folder: "+this.folderName);
            System.exit(-1);
        }
        initTermPosting();
//        long end = System.currentTimeMillis();
//        System.out.println("Positional index:" + (end-start));
    }


    public void initTermPosting() {
//        List<String> allUniqueTerms = new ArrayList<>();
        File[] contents = folder.listFiles();
        uniqueWordIndex = new HashMap<>();
        termPostings = new HashMap<>();
        docs = new String[numDoc];
        try {
            int clen = contents.length;
        } catch (NullPointerException e) {
            System.out.println(folder + " has no files");
            e.printStackTrace();
            System.exit(-1);
        }

        HashMap<String, ArrayList<Posting>> tempTermPostings = new HashMap<>();
        int uniqueWords = 0;
        String currentWord;
        for (int i = 0; i < contents.length; i++) {
            if (contents[i].isFile()) {
                String doc = contents[i].getName();
                docs[i]=doc;
                String[] words = pre.process(contents[i]);
                HashMap<String, ArrayList<Integer>> wordIndices = new HashMap<>();
                for (int j = 0; j < words.length; j++) {
                    currentWord = words[j];
                    ArrayList<Integer> newArr;
                    if (wordIndices.containsKey(currentWord)) {
                        newArr=wordIndices.get(currentWord);
                    }
                    else {
                        newArr=new ArrayList<>();
                        wordIndices.put(currentWord, newArr);
                    }
                    newArr.add(j);
                    if (!uniqueWordIndex.containsKey(currentWord)) {
//                        allUniqueTerms.add(currentWord);
                        uniqueWordIndex.put(currentWord, uniqueWords);
                        uniqueWords++;
                    }
                }
                for (String term: wordIndices.keySet()) {
//                	String[] temp = map.get(term).split("\\s");
//                	for (int j = 0; j < temp.length; j++) {
//                		pos[j] = Integer.parseInt(temp[j]);
//                	}
                    Posting p = new Posting(doc, wordIndices.get(term));
                    ArrayList<Posting> postings;
                    if (tempTermPostings.containsKey(term)) {
                        postings = tempTermPostings.get(term);
                        postings.add(p);
                    } else {
                        postings = new ArrayList<>();
                        postings.add(p);
                        tempTermPostings.put(term, postings);
                    }
                }
            }
        }
        for (String term: tempTermPostings.keySet()) {
            ArrayList<Posting> postings = tempTermPostings.get(term);
            Posting[] parr = postings.toArray(new Posting[postings.size()]);
            try{
                termPostings.put(term, parr);
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
//        this.uniqueWordIndex = uniqueWordIndex;
////        this.allUniqueTerms = allUniqueTerms;
    }

    public Set<String> allUniqueTerms(){
        return uniqueWordIndex.keySet();
    }

    public int termFrequency(String term, String doc) {
        Posting[] postings = termPostings.get(term);
        if (postings==null){
            return 0;
        }
        for (Posting posting : postings) {
            if (posting.doc.equals(doc)) {
                return posting.getPoss().length;
            }
        }
        return 0;
    }

    public int docFrequency(String term) {
        if (termPostings.containsKey(term)){
            return termPostings.get(term).length;
        }else{
            return 0;
        }
    }


    public String postingsList(String term) {
        if (!termPostings.containsKey(term)){
            return "[]";
        }
        Posting[] postings = termPostings.get(term);
        String repr = "[";
        for (int i = 0; i < postings.length; i++) {
            if (i != 0) {
                repr += ",";
            }

            Posting posting = postings[i];
            String docRepr = "<";
            docRepr += posting.doc;
            docRepr += ":";
            for (int j = 0; j < posting.getPoss().length; j++) {
                if (j != 0) {
                    docRepr += ",";
                }
                docRepr += posting.getPoss()[j];
            }
            docRepr += ">";

            repr += docRepr;
        }
        repr += "]";
        return repr;
    }



    public double TPScore(String query, String doc) {
        query=query.toLowerCase();
        String[] queryWords = query.split("\\s");
        if (queryWords.length == 1 || queryWords.length == 0) {
            return 0;
        }
        double denom = 0;
        for (int i = 0; i < queryWords.length - 1; i++) {
            String t1 = queryWords[i];
            String t2 = queryWords[i + 1];
            denom += dist(doc, t1, t2);
        }
        double ret = (double) queryWords.length / denom;
        return ret;
    }

    public double dist(String doc, String t1, String t2) {
        double arbitrary=171717;

        Posting[] postings1 = termPostings.get(t1);
        Posting[] postings2 = termPostings.get(t2);
        if (postings1==null || postings2==null){
            return arbitrary;
        }
        int[] poss1 = new int[0];
        int[] poss2 = new int[0];
        for (Posting posting : postings1) {
            if (posting.doc.equals(doc)) {
                poss1 = posting.getPoss();
                break;
            }
        }

        for (Posting posting : postings2) {
            if (posting.doc.equals(doc)) {
                poss2 = posting.getPoss();
                break;
            }
        }

        if (poss1.length == 0 || poss2.length == 0) {
            return arbitrary;
        }

        // we can do it in linear time, given that the postings are sorted.
        int i = 0;
        int j = 0;
        double minDiff = arbitrary;
        while (i != poss1.length && j != poss2.length ) {
            if (poss1[i] < poss2[j]) {
                int newDiff = poss2[j] - poss1[i];
                if (newDiff < minDiff) {
                    minDiff = newDiff;
                }
                i++;
            } else {
                j++;
            }
        }
        return minDiff;
    }

    public double VSScore(String query, String doc) {
        query=query.toLowerCase();
        // The query order is changed, which does not change the VSScore due to symmetry of cosine similarity.

        String[] queryWords = query.split("\\s");
        if (queryWords.length == 0) {
            return 0;
        }
        Set<String> querySet = new HashSet<>(Arrays.asList(queryWords));
        queryWords=querySet.toArray(new String[querySet.size()]);
        double[] vectorD = new double[queryWords.length];
        double[] vectorQ = new double[queryWords.length];

        String term;
        double docWeight, queryWeight, sum = 0;

        for (int i = 0; i < queryWords.length; i++) {
            term = queryWords[i];
            queryWeight = 0;
            for (int j = 0; j < queryWords.length; j++) {
                if (term.equals(queryWords[j]))
                    queryWeight += 1.0;
            }
            docWeight = weight(term, doc);
            sum = sum + docWeight * queryWeight;
            vectorD[i] = docWeight;
            vectorQ[i] = queryWeight;
        }

        double[] base = new double[queryWords.length];
        Arrays.fill(base, 0.0);
        double distD = vectorDist(base, vectorD);
        double distQ = vectorDist(base, vectorQ);


        if (distD*distQ==0){
            return 0;
        }
        return sum / (distD*distQ);
    }

    public double weight(String term, String doc) {
        if (docFrequency(term)==0){
            return 0;
        }
        return Math.sqrt(termFrequency(term, doc)) * Math.log10(((double) numDoc) / docFrequency(term));
    }

    public static double vectorLen(double[] array)
    {
        double[] base = new double[array.length];
        Arrays.fill(base, 0.0);
        return vectorDist(base, array);
    }

    public static double vectorDist(double[] array1, double[] array2)
    {
        double sum = 0.0;
        for(int i=0;i<array1.length;i++) {
            sum = sum + (array1[i]-array2[i])*(array1[i]-array2[i]);
        }
        return Math.sqrt(sum);
    }

    public double Relevance(String query, String doc) {
        query=query.toLowerCase();
        return 0.6*TPScore(query, doc) + 0.4*VSScore(query, doc);
    }
}

//class TDFT {
//	public String term;
//	public int docFreq;
//
//	public TDFT(String term, int dft) {
//		this.term = term;
//		this.docFreq = dft;
//	}
//}

class Posting {
    public String doc;
    private int[] poss;


    Posting(String doc, ArrayList<Integer> poss) {
        this.doc = doc;
        this.setPoss(poss);
    }


    public void setPoss(ArrayList<Integer> poss) {
        this.poss=new int[poss.size()];
        int last = Integer.MIN_VALUE;
        for (int i = 0; i <poss.size(); i++) {
            int pos=poss.get(i);
            if (pos < last) {
                System.out.println("You must sort the postings.");
                System.exit(-1);
            }
            this.poss[i]=pos;
            last = pos;
        }
    }

    public int[] getPoss() {
        return poss;
    }
}
