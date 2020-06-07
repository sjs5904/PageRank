import java.io.*;

public class SpamFarm {
	private final File graphFile;
	private int numSpamPages;
	private int totalNodes;
    String graphFileName;
    String targetString;
    int target;

    public SpamFarm(String graphFileName, int target, int numSpamPages) {
        this.graphFileName = graphFileName;
        this.target = target;
        this.graphFile = new File(graphFileName);
//        PageRank pr = new PageRank(graphFileName);
        targetString = target + "";
        this.numSpamPages = numSpamPages;
    }

    void createSpam(String fileName) throws IOException{
        File tempFile = new File(fileName);

        BufferedReader reader = new BufferedReader(new FileReader(graphFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        currentLine = reader.readLine();
        try {
        	totalNodes = Integer.parseInt(currentLine);
        } catch (NumberFormatException e) {
        	System.out.println(currentLine + " is not a valid");
        }
        writer.write(totalNodes + numSpamPages + System.getProperty("line.separator"));
        while((currentLine = reader.readLine()) != null) {
            writer.write(currentLine + System.getProperty("line.separator"));
        }

        for (int i = 1; i <= numSpamPages; i++) {
			int n = totalNodes + i;
			writer.write(n + " " + targetString + System.getProperty("line.separator"));
			writer.write(targetString + " "+ n + System.getProperty("line.separator"));
		}

        writer.close();
        reader.close();
    }
}
