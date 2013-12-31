package commands;

import java.util.ArrayList;
import java.util.List;

import util.DbUtils;

/**
 * Cleans up the format of judge answers:
 * - If the answer starts with "Answer:", removes this text. 
 * - If the answer has multiple parts separated by "or", treats these parts
 * 		as separate correct answers (unless one of the parts is "equivalent").
 * - Removes anything in parentheses.
 * - Removes non-ASCII characters and leading/trailing spaces.
 */
public class AnswerChanger
{
	public static List<String> getAnswers(String rawAnswer)
	{
		List<String> retval = new ArrayList<String>();
		String answer = rawAnswer.trim();
		if (answer.startsWith("Answer:"))
		{
			answer = answer.substring(7).trim();
		}

		String[] parts = answer.split("[\\(\\[;]");
		answer = parts[0];
		answer = DbUtils.removeNonAscii(answer);

		parts = answer.split(" or ");
		for (String part : parts)
		{
			if (!part.contains("equivalent") && part.trim().length() > 0)
				retval.add(part.trim());
		}

		return retval;
	}
}
