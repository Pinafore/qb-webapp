package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import util.FileUtils;

/**
 * Cleans up each line in a file by lowercasing it and removing
 * all non alphanumeric characters (except for spaces).
 *
 */
public class Tokenizer
{
	public static void main(String[] args) throws Exception
	{
		String inputFile = "C:/corpus/quizbowl/selected-questions.txt";
		String outputFile = "C:/corpus/quizbowl/selected-questions-tokenized.txt";
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		List<String> lines = FileUtils.readAll(inputFile);
		int i = 0;
		for (String line : lines)
		{
			System.out.println(i);	
			i++;
			String newLine = StringUtils.fixString(line);
			writer.write(newLine + "\n");
		}
		writer.close();
	}

}
