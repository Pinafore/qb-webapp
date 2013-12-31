package analysis;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import util.FileUtils;

/**
 * Creates a set of prior mean files based on the two-tier buzzer weighting
 * scheme: For each feature/class pair, 
 * 		mu = (lambda) * (tf-idf) + (gamma) * (tf-idf) * (number of buzzes).
 * Creates these files for a range of lambda and gamma values.
 * 
 * Requires two existing sets of prior mean files. Use BuzzerWeightGenerator to
 * generate the buzzer weights, and TfIdfWeightGenerator to generate the tf-idf
 * weights. 
 */
public class TieredWeightDuplicator
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
		
		for (int multI = 1; multI <= 20; multI++)
		{
			System.out.println(multI);
			for (int gammaI = 0; gammaI <= 20; gammaI++)
			{
				System.out.println(gammaI);
				double mult = multI * 1.0 / 10;
				double gamma = gammaI * 1.0 / 10; 
				
				String filename = "C:/corpus/quizbowl/weights/weight" + multI + "-" + gammaI;
				FileWriter writer = new FileWriter(filename);
						
				for (int i = 0; i < words.size(); i++)
				{
					writer.write(words.get(i));
					
					String str = "";
					for (int j = 0; j < tParts.get(i).size(); j++)
					{
						double value = mult * tParts.get(i).get(j);
						if (bParts.get(i).get(j) > 0)
							value += gamma * tParts.get(i).get(j) * bParts.get(i).get(j);
				
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
	
	
}
