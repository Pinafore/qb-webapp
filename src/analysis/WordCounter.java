package analysis;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import util.DbUtils;

/**
 * Counts the words in each question in the DB.
 */
public class WordCounter
{
	public static void main(String[] args) throws Exception
	{
		Connection connection = DbUtils.getConnection();
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		
		FileWriter writer = new FileWriter("C:/corpus/quizbowl/words.out");
		
		ResultSet rs = statement.executeQuery("select * from questions");
		while (rs.next())
		{
			int id = rs.getInt("question_id");
			String body = rs.getString("body");
			String[] parts = body.split("\\s");
			writer.write(id + "," + parts.length + "\n");
		}
		writer.close();
		
	}
}
	
