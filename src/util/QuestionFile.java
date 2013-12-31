package util;

import java.util.List;

/**
 * Class for storing the special answers from questionFile.txt. NOTE: This
 * list is set up statically, so if you change questionFile.txt, you
 * must restart the server!
 */
public class QuestionFile
{
	private static List<String> answers = null;

	static
	{
		setUp();
	}

	public static void setUp()
	{
		try
		{
			answers = FileUtils.readAll(Constants.QUESTION_FILE_LOCATION);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static List<String> getAnswers()
	{
		return answers;
	}
}
