package commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.DbUtils;
import util.FileUtils;

/**
 * Parses the questions and answers from a question dump file with a specific format
 * and puts them in the DB.
 */
public class DumpParser
{
	public static void main(String[] args) throws Exception
	{
		Connection connection = DbUtils.getConnection();
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		
		statement.executeUpdate("delete from answers where question_id >= 100000");
		statement.executeUpdate("delete from questions where question_id >= 100000");
		
		Set<String> tourns = getTournaments(statement);
		
		String filename = "C:/corpus/dump.txt";
		List<String> lines = FileUtils.readAll(filename);
		System.out.println(lines.size());
		
		String category = "Unknown";
		String author = "Unknown";
		int round = 0;
		
		int maxLines = lines.size();
		int id = 100000;
		for (int i = 0; (i+3) < maxLines; i += 5)
		{
			String ansLine = lines.get(i+3);
			String quesLine = lines.get(i+2);
			String tournLine = lines.get(i);
			
			ansLine = DbUtils.removeNonAscii(ansLine);
			quesLine = DbUtils.removeNonAscii(quesLine);
			tournLine = DbUtils.removeNonAscii(tournLine);
			
			if (ansLine.startsWith("ANSWER"))
			{
				ansLine = ansLine.substring(7).trim();
			}
			else
			{
				throw new Exception("Bad answer line " + (i+3) + ": " + ansLine); 
			}
			
			int index = quesLine.indexOf(' ');
			if (index > 0)
			{
				quesLine = quesLine.substring(quesLine.indexOf(' ')).trim();
			}
			else
			{
				throw new Exception("Bad question line " + (i+2) + ": " + quesLine);
			}
			
			List<String> answers = AnswerChanger.getAnswers(ansLine);
			if (answers.size() > 0)
			{
				if (!tourns.contains(tournLine.trim()))
				{
					id++;
					System.out.println(id);
					String sql = "insert into questions(question_id, body, category, author, "
							+ "round, tournament) values(?,?,?,?,?,?)";
					PreparedStatement prep = connection.prepareStatement(sql);
					prep.setInt(1, id);
					prep.setString(2, quesLine);
					prep.setString(3, category);
					prep.setString(4, author);
					prep.setInt(5, round);
					prep.setString(6, tournLine.trim());
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
			else
			{
				System.out.println("No answers found in line " + (i+3) + ": " + ansLine);
			}
			

			
			
		}
		
	}
	
	private static Set<String> getTournaments(Statement stmt) throws Exception
	{
		Set<String> tourns = new HashSet<String>();
		
		ResultSet rs = stmt.executeQuery("select distinct tournament from questions");
		while (rs.next())
		{
			tourns.add(rs.getString("tournament").trim());
			System.out.println(rs.getString("tournament").trim());
		}
		return tourns;
	
	}

}
