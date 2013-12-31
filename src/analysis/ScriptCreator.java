package analysis;

import java.io.FileWriter;

/**
 * Creates a set of scripts to run the megam classifier with different prior
 * mean files. Useful for running on the cluster.
 */
public class ScriptCreator
{

	public static void main(String[] args) throws Exception
	{
		String command="";
		for (int multI = 1; multI <= 200; multI++)
		{
			System.out.println(multI);
			for (int gammaI = 10; gammaI <= 10; gammaI++)
			{
				String scriptName = "C:/corpus/quizbowl/scripts/script" + multI + "-" + gammaI;
				
				FileWriter writer = new FileWriter(scriptName);
				String filename = "/chomes/bsonrisa/quizbowl/weights/weight" + multI + "-" + gammaI;

				
				command = "/fs/clip-software/megam_0.92/bin/megam.opt " +
					"-mean " + filename + " multiclass " +
					" /chomes/bsonrisa/quizbowl/selected-questions-200.txt > /dev/null";
				writer.write(command + "\n");

				
				writer.close();
			}
		}
	}

}
