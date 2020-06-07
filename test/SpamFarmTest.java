import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

public class SpamFarmTest {

	private PageRank pr = new PageRank(DataPath.dataPath+"/correctGraph.txt", 0.1, 0.85);
	private int target = Integer.parseInt(pr.nodeName[pr.getMinIndex(pr.pageRank())]);

	void CreateAllSpamFarm(int numSpamPages) {
		System.out.println("Target node name : " + target);
		SpamFarm sf = new SpamFarm(DataPath.dataPath+"/correctGraph.txt", target, numSpamPages);
        try {
			sf.createSpam(DataPath.dataPath+"/testfile.txt");
//			System.out.println(pr.nodeName[target - 1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void FarmTargetPageRank(int numSpamPages) {
		System.out.println("number of spam pages: "+numSpamPages);
		CreateAllSpamFarm(numSpamPages);
		int index=pr.nameNode.get(""+target);
		System.out.println("Internal node index: "+index);
		PageRank spamRank = new PageRank(DataPath.dataPath+"/testfile.txt", 0.1, 0.85);


		double[] trust=pr.trustRank(pr.pageRank());
		double[] spamTrust=spamRank.trustRank(spamRank.pageRank());

		System.out.println("Page rank of target pr   : " + pr.pageRankOf(target));
		System.out.println("Page rank of target spamRank  : " + spamRank.pageRankOf(target));

		System.out.println("Page trust rank of target pr  : " + pr.trustRank[pr.nameNode.get(""+target)]);
		System.out.println("Page trust rank of target spamRank : " + spamRank.trustRank[spamRank.nameNode.get(""+target)]);

		TopKHelper t = new TopKHelper();
		t.topK(pr.pageRank(), 10);
		System.out.println("Top 10 Page Rank of pr   : " + Arrays.toString(pr.indicesToNames(t.indices)));
		System.out.println(t.placement(pr.pageRank(),index));
//		System.out.println(Arrays.toString(t.values));
		t.topK(spamRank.pageRank(), 10);
		System.out.println("Top 10 Page Rank of spamRank  : " + Arrays.toString(pr.indicesToNames(t.indices)));
		System.out.println(t.placement(spamRank.pageRank(),index));
//		System.out.println(Arrays.toString(t.values));

		t.topK(trust, 10);
		System.out.println("Top 10 Trust Rank of pr  : " + Arrays.toString(pr.indicesToNames(t.indices)));
		System.out.println(t.placement(trust,index));
//		System.out.println(Arrays.toString(t.values));

		t.topK(spamTrust, 10);
		System.out.println("Top 10 Trust Rank of spamRank : " + Arrays.toString(spamRank.indicesToNames(t.indices)));
		System.out.println(t.placement(spamTrust,index));
//		System.out.println(Arrays.toString(t.values));

	}

	@Test
	void FTPRAll(){
		FarmTargetPageRank(10);
		FarmTargetPageRank(100);
		FarmTargetPageRank(1000);
	}
}
