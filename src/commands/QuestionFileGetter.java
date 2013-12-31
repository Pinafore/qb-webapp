package commands;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import util.Constants;
import util.DbUtils;

/**
 * Populates questionFile.txt by adding all answers that correspond
 * to <minFreq> or more questions.
 */
public class QuestionFileGetter
{
	static int minFreq = 5;
	
	public static void main(String[] args) throws Exception
	{
		Connection connection = DbUtils.getConnection();
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
	
		String sql = "select answer_text from answer_view where theCount >= " + minFreq;
		ResultSet rs = statement.executeQuery(sql);
		FileWriter fw = new FileWriter(Constants.QUESTION_FILE_LOCATION);
		while (rs.next())
		{
			String answer = rs.getString("answer_text");
			answer = answer.trim();
			if (answer.length() > 0)
				fw.write(answer + "\n");
		}
		fw.close();

	}
}
