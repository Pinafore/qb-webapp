package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;

public class DbUtils
{
	public static Connection getConnection() throws Exception
	{
		Class.forName("org.sqlite.JDBC");
		String location = "jdbc:sqlite:" + Constants.DB_LOCATION;
		return DriverManager.getConnection(location);
	}
	
	public static void tryCloseConnection(Connection conn)
	{
		try
		{
			if (conn != null)
			{
				conn.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String createOptions(String columnName, Collection<String> options)
	{
		String str = "1=0";
		for (String option : options)
		{
			str += " or " + columnName + "='" + option + "'";
		}
		return str;
	}
	
	public static String sanitize(String str)
	{
		return str.replaceAll("[^\\w\\s]", "");
	}

	public static String removeNonAscii(String str)
	{
		String output = str.replaceAll("[^\\p{ASCII}]", "?");
		if (!output.equals(str))
		{
			System.out.println(str + " -> " + output);
		}
		return output;
	}
	
	public static int getNumQuestions(Statement stmt, String username) throws Exception
	{
		String sql = "select count(*) as theCount from answers where username = '" + username + "'";
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		return rs.getInt("theCount");
	}

}
