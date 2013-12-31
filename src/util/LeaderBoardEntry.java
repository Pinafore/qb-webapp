package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one entry in a leader board.
 */
public class LeaderBoardEntry
{
	public String username;
	public int numAnswers;
	public int numCorrect;
	public String avgWords; // string, not double, so we can format it

	/**
	 * Builds the leader board from the database.
	 */
	public static List<LeaderBoardEntry> getLeaderBoard()
	{
		List<LeaderBoardEntry> entries = new ArrayList<LeaderBoardEntry>();
		DecimalFormat df = new DecimalFormat("#.##");
		try
		{
			Connection conn = DbUtils.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from leader_view order by numAnswers desc");
			while (rs.next())
			{
				LeaderBoardEntry entry = new LeaderBoardEntry();
				entry.username = rs.getString("username");
				entry.numAnswers = rs.getInt("numAnswers");
				entry.numCorrect = rs.getInt("numCorrect");
				
				double avgWords = rs.getDouble("avgNumWords");
				entry.avgWords = df.format(avgWords);
				
				entries.add(entry);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return entries;
	}
	
}
