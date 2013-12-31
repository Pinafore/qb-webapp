package analysis;

/**
 * Contains a method to "clean up" a string by lowercasing it and removing
 * all non alphanumeric characters (except for spaces).
 */
public class StringUtils
{
	public static String fixString(String line)
	{
		String newLine = "";
		line = line.toLowerCase();
		String[] parts = line.split("\\s");
		for (String part : parts)
		{
			part = part.replaceAll("[^a-zA-Z0-9]", "");
			if (part.length() > 0)
				newLine += part + " ";
		}
		return newLine.trim();
	}

}
