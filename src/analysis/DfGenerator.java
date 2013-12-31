package analysis;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.FileUtils;

/**
 * Generates the document frequencies for each feature. Used for calculating tf-idf.
 *
 */
public class DfGenerator
{
	public static Map<String, Integer> getDocFreqs(String filename) throws Exception
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<String> lines = FileUtils.readAll(filename);
		for (String line : lines)
		{
			String[] parts = line.split(" ");
			map.put(parts[0], Integer.parseInt(parts[1]));
		}
		return map;
	}
	
	public static Map<String, Integer> generateDocFreqs(List<String> lines) throws Exception
	{
		String outputFile = "C:/corpus/quizbowl/selected-df.txt";
		
		Map<String, Integer> df = new HashMap<String, Integer>();
		
		for (String line : lines)
		{
			
			
			Set<String> docWords = new HashSet<String>();
			String[] parts = line.split(" ");
			for (int i = 1; i < parts.length; i++)
			{
				docWords.add(parts[i]);
			}
			
			for (String word : docWords)
				MapUtils.addToMap(df,word);
		}
		
		
		FileWriter writer = new FileWriter(outputFile);
		List<String> wordOrder = MapUtils.sortByValue(df);
		for (String word : wordOrder)
		{
			writer.write(word + " " + df.get(word) + "\n");
		}
		writer.close();
		return df;
	}

}
