package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.FileUtils;

/**
 * Creates a prior mean file in which the prior mean for each (feature, class) 
 * pair is the tf-idf for the feature. Used for the (X)WeightDuplicator classes.
 */
public class TfIdfWeightGenerator
{
	public static void generate(Map<String, List<String>> ansMap, double multFactor) throws Exception
	{
		DecimalFormat decf = new DecimalFormat("0.00");
		
		String outputFile = "C:/corpus/quizbowl/weights/tfidf-weights-" + decf.format(multFactor) + ".txt";
		String dfFile = "C:/corpus/quizbowl/selected-df.txt";
		String buzzerFile = "C:/corpus/quizbowl/buzzer.txt";
		
		Set<String> stopwords = new HashSet<String>(
				FileUtils.readAll("C:/corpus/quizbowl/stopwords.txt"));
		

		Map<String, Integer> df = DfGenerator.getDocFreqs(dfFile);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		List<String> lines = null;//FileUtils.readAll(inputFile); 
			
		int maxClass = 0;
		Set<String> features = new HashSet<String>();
		
		// up to 10000 classes
		Map<String,Integer>[] tfidf = new Map[10000];
		int[] docSizes = new int[10000];
		for (int i = 0; i < 10000; i++)
			tfidf[i] = new HashMap<String,Integer>();
		
		for (String line : lines)
		{
			if (line.equals("DEV") || line.equals("TEST"))
				continue;
			String[] parts = line.split(" ");
			int classNum = Integer.parseInt(parts[0]);
			maxClass = Math.max(maxClass, classNum);
			docSizes[classNum] += parts.length - 1;
			
			for (int i = 1; i < parts.length; i++)
			{
				MapUtils.addToMap(tfidf[classNum], parts[i]);
				features.add(parts[i]);
			}
		}
		
		for (String feature : features)
		{
			System.out.println(feature);
			String str = feature;
			for (int i = 0; i <= maxClass; i++)
			{
				double tf = 0;
				Map<String,Integer> map = tfidf[i];
				if (map.containsKey(feature))
				{
					tf = map.get(feature) * 1.0 / docSizes[i];
				}
				
				// this is a hack - actually adjust the df's for the proper size
				int dfCount = Math.max(df.get(feature), lines.size()-2);
				double idf = -Math.log(df.get(feature) * 1.0 / dfCount);
				
				// the -2 is so we don't count DEV and TEST
				str += " " + (tf * idf);
				
			}
			writer.write(str + "\n");
		}

		writer.close();
	}

}
