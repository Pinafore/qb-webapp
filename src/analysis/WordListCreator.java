package analysis;

import java.io.FileWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import util.FileUtils;

/**
 * Creates a list of all features found in all questions.
 */
public class WordListCreator
{
	public static void main(String[] args) throws Exception
	{
		String inputFile = "C:/corpus/quizbowl/selected-questions-tokenized.txt";
		String outputFile = "C:/corpus/quizbowl/selected-wordlist.txt";
		
		FileWriter writer = new FileWriter(outputFile);
		List<String> lines = FileUtils.readAll(inputFile);
		Set<String> words = new TreeSet<String>();
		
		for (String line : lines)
		{
			String[] parts = line.split(" ");
			for (String word : parts)
				words.add(word);
		}
		
		for (String word : words)
		{
			writer.write(word + "\n");
		}
		writer.close();
	}

}
