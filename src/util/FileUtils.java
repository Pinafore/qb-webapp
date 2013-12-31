package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for handling files.
 */
public class FileUtils
{

	/**
	 * Reads all lines in a file and puts them in a list.
	 */
	public static List<String> readAll(String filename) throws Exception
	{
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = "";
		while ((line = reader.readLine()) != null)
			lines.add(line);
		reader.close();
		return lines;
	}
}
