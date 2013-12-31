package analysis;

/**
 * Creates the qsub commands for submitting the scripts created by ScriptCreator
 * to be run on the cluster.
 */
public class Qsub
{
	public static void main(String[] args)
	{
		for (int multI = 1; multI <= 200; multI++)
		{
			for (int gammaI = 10; gammaI <= 10; gammaI++)
			{
				String scriptName = "scripts/script" + multI + "-" + gammaI;
				System.out
						.println("qsub -l mem=1GB,walltime=24:00:00 -q batch "
								+ scriptName);
			}
		}
	}

}
