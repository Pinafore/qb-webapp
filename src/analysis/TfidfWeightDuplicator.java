package analysis;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import util.FileUtils;

/*
 * Creates a set of prior mean files based on the tf-idf weighting
 * scheme: For each feature/class pair, mu = (multiplier) * (tf-idf).
 * Creates these files for a range of multipliers.
 * 
 * Requires an existing set of prior mean files. Use  TfIdfWeightGenerator
 * to generate these tf-idf weights. 
 */
public class TfidfWeightDuplicator
{
	public static void main(String[] args) throws Exception
	{
		String tfidfFile = "C:/corpus/quizbowl/weights/tfidf-weights.txt";
		List<String> tfidfLines =  FileUtils.readAll(tfidfFile);
		
		List<String> words = new ArrayList<String>();
		List<List<Double>> tParts = new ArrayList<List<Double>>();
		
		for (int i = 0; i < tfidfLines.size(); i++)
		{
			List<Double> tl = new ArrayList<Double>();
			
			String[] tp = tfidfLines.get(i).split(" ");
			words.add(tp[0]);
			
			for (int j = 1; j < tp.length; j++)
			{
				tl.add(Double.parseDouble(tp[j]));
			}
			
			tParts.add(tl);
		}
		
		for (int multI = 1; multI <= 200; multI++)
		{
			System.out.println(multI);
			double mult = multI * 0.1;
			String filename = "C:/corpus/quizbowl/weights/weight" + multI + "-10";
			FileWriter writer = new FileWriter(filename);
					
			for (int i = 0; i < words.size(); i++)
			{
				writer.write(words.get(i));
				
				String str = "";
				for (int j = 0; j < tParts.get(i).size(); j++)
				{
					double value = 0;
					value = mult * tParts.get(i).get(j);
					
					if (value == 0)
						str += " 0";
					else
						str += " " + value;
				}

				writer.write(str + "\n");
				
			}
			writer.close();
		}
		
	}
	
	
}
