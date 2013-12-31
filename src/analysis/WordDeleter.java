package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import util.FileUtils;

/**
 * Removes words that only occur in one question, from the text of that question.
 */
public class WordDeleter
{
	public static void main(String[] args) throws Exception
	{	
		String inputFile = "C:/corpus/quizbowl/selected-questions-tokenized.txt";
		String outputFile = "C:/corpus/quizbowl/selected-questions-tokenized-small.txt";
		String dfFile = "C:/corpus/quizbowl/selected-df.txt";
		
		Map<String, Integer> df = DfGenerator.getDocFreqs(dfFile);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		List<String> lines = FileUtils.readAll(inputFile);
		int i = 0;
		for (String line : lines)
		{
			System.out.println(i);
			String newLine = "";
			String[] parts = line.split(" ");
			for (String part : parts)
			{
				if (df.get(part) > 1 || i % 2 == 1) // keep all answers
				{
					newLine += part + " ";
				}
			}
			writer.write(newLine + "\n");
			i++;
		}
		writer.close();
	}

}
