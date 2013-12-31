package commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import util.DbUtils;

/**
 * Removes non-ASCII characters from all questions in the DB.
 */
public class AsciiRemover
{
	public static void main(String[] args) throws Exception
	{
		Connection connection = DbUtils.getConnection();
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		
		PreparedStatement prep = connection.prepareStatement("update questions set body = ? where question_id = ?");
		
		ResultSet rs = statement.executeQuery("select question_id, body from questions");
		while (rs.next())
		{
			String body = rs.getString("body");
			int id = rs.getInt("question_id");
			System.out.println(id);
			body = DbUtils.removeNonAscii(body);
			prep.setString(1,body);
			prep.setInt(2,id);
			prep.execute();
		}
	}
	
}
