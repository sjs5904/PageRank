import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class QueryProcessorTest {
    QueryProcessor qp=new QueryProcessor(DataPath.dataPath+"/IR");

    @Test
    void topKDocs1() {
        QueryProcessor qp=new QueryProcessor(DataPath.dataPath+"/IR");
        ArrayList<String> ret=qp.topKDocs("world series", 5);
        System.out.println(ret.toString());
    }

    @Test
    void topKDocs2() {
        QueryProcessor qp=new QueryProcessor("./test resources/files");
        ArrayList<String> ret=qp.topKDocs("a aa", 3);
        System.out.println(ret.toString());
    }

    void topKDocs(String query){
        System.out.println(query);
        System.out.println("Document & Relevance & TPScore & VSScore\\\\");
        System.out.println("\\hline");
        ArrayList<String> ret=qp.topKDocs(query , 10);
        for (int i = 0; i < ret.size(); i++) {
            String doc=ret.get(i);
            String replace=doc.replace("_","\\_");
            replace=replace.replace("&", "\\&");
            replace=replace.replace("%", "\\&");
            System.out.printf("%50s & %10.5f & %10.5f & %10.5f \\\\ \n ",replace, qp.Relevance(query, doc), qp.TPScore(query, doc),
                    qp.VSScore(query,doc));
        }
        System.out.println();
    }

    @Test
    void topKDocsTest(){
        topKDocs("national");
        topKDocs("world series");
        topKDocs("Major League Baseball");
        topKDocs("the group of batters ");
        topKDocs("members of the 500");
    }
}