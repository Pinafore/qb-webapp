package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.FileUtils;

public class BlankWeightGenerator
{
	public static void main(String[] args) throws Exception
	{
		
		String inputFile = "C:/corpus/quizbowl/selected-questions-200.txt";
		String outputFile = "C:/corpus/quizbowl/init-weights.txt";
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		List<String> lines = FileUtils.readAll(inputFile); 
			
		int maxClass = 0;
		Set<String> features = new HashSet<String>();
		
		for (String line : lines)
		{
			if (line.equals("DEV") || line.equals("TEST"))
				continue;
			String[] parts = line.split(" ");
			maxClass = Math.max(maxClass, Integer.parseInt(parts[0]));
			for (int i = 1; i < parts.length; i++)
			{
				features.add(parts[i]);
			}
		}
		
		for (String feature : features)
		{
			String str = feature;
			for (int i = 0; i <= maxClass; i++)
			{
				str += " 0";
			}
			writer.write(str + "\n");
		}

		writer.close();
	}

}
