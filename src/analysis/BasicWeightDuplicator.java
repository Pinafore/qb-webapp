package analysis;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import util.FileUtils;

/**
 * Creates a set of prior mean files based on the single-tier buzzer weighting
 * scheme: For each feature/class pair, 
 * 		mu = (multiplier) * (tf-idf) * (number of buzzes).
 * Creates these files for a range of multipliers.
 * 
 * Requires two existing sets of prior mean files. Use BuzzerWeightGenerator to
 * generate the buzzer weights, and TfIdfWeightGenerator to generate the tf-idf
 * weights. 
 */
public class BasicWeightDuplicator
{
	public static void main(String[] args) throws Exception
	{
		String tfidfFile = "C:/corpus/quizbowl/weights/tfidf-weights.txt";
		String buzzFile = "C:/corpus/quizbowl/weights/buzzer-weights.txt";
		List<String> tfidfLines =  FileUtils.readAll(tfidfFile);
		List<String> buzzLines = FileUtils.readAll(buzzFile);
		
		List<String> words = new ArrayList<String>();
		List<List<Double>> tParts = new ArrayList<List<Double>>();
		List<List<Double>> bParts = new ArrayList<List<Double>>();
		
		for (int i = 0; i < tfidfLines.size(); i++)
		{
			List<Double> tl = new ArrayList<Double>();
			List<Double> bl = new ArrayList<Double>();
			
			String[] tp = tfidfLines.get(i).split(" ");
			String[] bp = buzzLines.get(i).split(" ");
			words.add(tp[0]);
			
			for (int j = 1; j < bp.length; j++)
			{
				tl.add(Double.parseDouble(tp[j]));
				bl.add(Double.parseDouble(bp[j]));
			}
			
			tParts.add(tl);
			bParts.add(bl);
		}
		
		for (int multI = 1; multI <= 50; multI++)
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
					if (bParts.get(i).get(j) == 0)
						value = 0;
					else
						value = mult * tParts.get(i).get(j) * bParts.get(i).get(j);
					
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
