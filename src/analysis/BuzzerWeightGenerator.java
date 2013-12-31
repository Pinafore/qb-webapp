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
 * pair is the number of buzzes for that feature (word) in questions of that 
 * class (answer). Used for the (X)WeightDuplicator classes.
 */
public class BuzzerWeightGenerator
{
	public static void generate(Map<String, List<String>> ansMap) throws Exception
	{

		Set<String> stopwords = new HashSet<String>(
				FileUtils.readAll("C:/corpus/quizbowl/stopwords.txt"));
		
		
		String outputFile = "C:/corpus/quizbowl/weights/tfidf-weights.txt";
		String buzzerOut = "C:/corpus/quizbowl/weights/buzzer-weights.txt";
		
		String dfFile = "C:/corpus/quizbowl/selected-df.txt";
		String buzzerFile = "C:/corpus/quizbowl/buzzer.txt";

		Map<String, Integer> df = DfGenerator.getDocFreqs(dfFile);
		Map<String, Map<String, Integer>> buzzMap = BuzzerFileGenerator.getBuzzedWords(buzzerFile);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		BufferedWriter buzzWriter = new BufferedWriter(new FileWriter(buzzerOut));
		
		int numClasses = ansMap.size();
		Set<String> features = new HashSet<String>();
		
		// up to 10000 classes
		Map<String,Integer>[] tfidf = new Map[10000];
		int[] docSizes = new int[10000];
		String[] answers = new String[10000];
		for (int i = 0; i < 10000; i++)
			tfidf[i] = new HashMap<String,Integer>();
		
		int numDocs = 0;
		
		double theMax = 0, theMin = 100000, theSum = 0, theCount = 0;
		
		int classNum = 0;
		for (String answer : ansMap.keySet())
		{
			answers[classNum] = answer;
			List<String> quesList = ansMap.get(answer);
			
			for (String line : quesList)
			{
				numDocs++;
				String[] parts = line.split("\\s");
				docSizes[classNum] += parts.length - 1;
				
				for (int i = 1; i < parts.length; i++)
				{
					MapUtils.addToMap(tfidf[classNum], parts[i]);
					features.add(parts[i]);
				}
			}

			classNum++; // TODO: make sure these match!
		}
		
		for (String feature : features)
		{
			String str = feature;
			String buzzStr = feature;
			for (int i = 0; i < numClasses; i++)
			{
				String answer = answers[i];
				
				if (!stopwords.contains(answer))
				{
					double tf = 0;
					Map<String,Integer> map = tfidf[i];
					if (map.containsKey(feature))
					{
						tf = map.get(feature) * 1.0 / docSizes[i];
					}
					
					// this is a hack - actually adjust the df's for the proper size
					int dfCount = Math.max(df.get(feature), numDocs);
					double idf = -Math.log(df.get(feature) * 1.0 / dfCount);
					
					double value = tf*idf;
					
					if (buzzMap.containsKey(answer) && buzzMap.get(answer).containsKey(feature))
					{
						int freq = buzzMap.get(answer).get(feature);
						buzzStr += " " + freq;
					}
					else
						buzzStr += " 0";
					
					if (value == 0)
						str += " 0";
					else
						str += " " + value;
					
					theMax = Math.max(theMax, tf*idf);
					theMin = Math.min(theMin, tf*idf);
					theSum += tf*idf;
					theCount++;
				}
				else
				{
					str += " 0";
					buzzStr += " 0";
				}
				
			}
			writer.write(str + "\n");
			buzzWriter.write(buzzStr + "\n");
		}

		writer.close();
		buzzWriter.close();
		
		System.out.println("Max tf/idf: " + theMax);
		System.out.println("Min tf/idf: " + theMin);
		System.out.println("Avg tf/idf: " + theSum / theCount);
		System.out.println("Tf/idf count: " + theCount);
	}

}
