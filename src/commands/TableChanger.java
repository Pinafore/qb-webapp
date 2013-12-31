package commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.Constants;
import util.DbUtils;

/**
 * Converts the "old_questions" table to the separate "questions" and "answers" tables. 
 */
public class TableChanger
{
	public static void main(String[] args) throws Exception
	{

		Connection connection = DbUtils.getConnection();
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.

		statement.executeUpdate("drop table if exists person");
		statement.executeUpdate("delete from questions");
		statement.executeUpdate("delete from answers");

		String sql = "select * from old_questions";
		// sql += " where category = 'History -- European'";

		ResultSet rs = statement.executeQuery(sql);
		int id = 0;

		while (rs.next())
		{
			List<String> answers = AnswerChanger.getAnswers(rs.getString("answer"));
			if (answers.size() > 0)
			{
				String body = DbUtils.removeNonAscii(rs.getString("body"));
				
				id++;
				System.out.println(id);
				sql = "insert into questions(question_id, body, category, author, "
						+ "round, tournament) values(?,?,?,?,?,?)";
				PreparedStatement prep = connection.prepareStatement(sql);
				prep.setInt(1, id);
				prep.setString(2, body);
				prep.setString(3, rs.getString("category"));
				prep.setString(4, rs.getString("author"));
				prep.setInt(5, rs.getInt("round"));
				prep.setString(6, rs.getString("tournament"));
				prep.execute();
	
				for (String answer : answers)
				{
					sql = "insert into answers(username, question_id, answer_date, "
							+ "answer_text, correct, num_words, is_reference) values('default',?,0,?,1,0,1)";
					prep = connection.prepareStatement(sql);
					prep.setInt(1, id);
					prep.setString(2, answer);
					prep.execute();
				}
			}

		}

	}

}
