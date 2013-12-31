package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import util.FileUtils;

/**
 * Generates a list of buzzed features. 
 * Input format:
 * Line 1 + 3n: Question text
 * Line 2 + 3n: Answer text
 * Line 3 + 3n: Number of words seen before the buzz
 * 
 * Output format:
 * Line 1 + 2n: Answer text
 * Line 2 + 2n: Each buzzed word followed by its number of buzzes, all space separated.
 * 		Example: black 3 brown 4 green 2 ...
 * All of the output will be lower-case and with non-alphanumeric characters removed.
 * 
 * A stopword list (one stopword per line) is also needed.
 */
public class BuzzerFileGenerator
{
	/**
	 * How many words before the buzz should we include?
	 */
	final static int MAX_WORDS = 5;
	
	public static void main(String[] args) throws Exception
	{		
		String inputFile = "C:/corpus/quizbowl/answers-good.txt";
		String outputFile = "C:/corpus/quizbowl/buzzer.txt";
		
		Set<String> stopwords = new HashSet<String>(
				FileUtils.readAll("C:/corpus/quizbowl/stopwords.txt"));
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		List<String> lines = FileUtils.readAll(inputFile);
		
		Map<String, Map<String, Integer>> words = new TreeMap<String, Map<String, Integer>>();
		
		for (int i = 0; i < lines.size(); i += 3)
		{
			String quesLine = lines.get(i);
			//System.out.println(quesLine);
			String ansLine = StringUtils.fixString(lines.get(i+1));
			int numWords = Integer.parseInt(lines.get(i+2));
			
			String[] parts = quesLine.split("\\s");
			// TODO: fix this? why is numWords >= the length?
			numWords = Math.min(numWords,parts.length);
			
			int start = Math.max(0, numWords-MAX_WORDS);
			for (int j = start; j < numWords; j++)
			{
				String fixedPart = StringUtils.fixString(parts[j]);
				if (fixedPart.length() > 0 && !stopwords.contains(fixedPart))
					MapUtils.addToMapMap(words, ansLine, fixedPart);
			}
		}
		
		for (String ans : words.keySet())
		{
			//writer.write(ans + "----");
			writer.write(ans + "\n");
			for (String word : words.get(ans).keySet())
				writer.write(word + " " + words.get(ans).get(word) + " ");
			writer.write("\n");
		}
		
		writer.close();
	}
	
	public static Map<String, Map<String, Integer>> getBuzzedWords(String filename) throws Exception
	{
		List<String> lines = FileUtils.readAll(filename);
		Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
		for (int i = 0; i < lines.size(); i+= 2)
		{
			String answer = lines.get(i);
			String[] parts = lines.get(i+1).split(" ");
			for (int j = 0; j < parts.length; j+= 2)
			{
				String word = parts[j];
				int freq = Integer.parseInt(parts[j+1]);
				for (int k = 0; k < freq; k++)
					MapUtils.addToMapMap(map,answer,word);
			}
		}
		return map;
	}
}
