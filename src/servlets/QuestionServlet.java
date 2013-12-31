package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import util.Constants;
import util.DbUtils;
import util.QuestionFile;

/**
 * This servlet is called when the user wants a new question.
 */
public class QuestionServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QuestionServlet()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	private void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		Connection conn = null;
		try
		{
			conn = DbUtils.getConnection();
			Statement stmt = conn.createStatement();
			
			String username = DbUtils.sanitize(request.getParameter("username"));
			setUpOptions(request.getSession(true), 
					request.getParameter("hideTournament"),
					request.getParameter("hideCategory"),
					request.getParameter("resetOptions"));
			String optionStr = getOptions(request.getSession());
			
			JSONObject obj = null;
			while (obj == null)
			{			
				// What's the next answer we should ask the user about?
				String nextAns = getNextAnswer(stmt, username);
				
				if (nextAns == null) // user is done with the special answers
				{
					// get a random row from the question table
					String sql = "select * from questions " + optionStr +
						" order by random() limit 1";
					ResultSet rs = stmt.executeQuery(sql);
					obj = resultSetToJSON(rs);
				}
				else // user is still doing special answers
				{
					String sql = "select * from questions " + optionStr + 
						" and question_id in (select question_id from answers where" +
						" answer_text = ?) order by random() limit 1";
					PreparedStatement prep = conn.prepareStatement(sql);
					prep.setString(1, nextAns);
					ResultSet rs = prep.executeQuery();
					obj = resultSetToJSON(rs);	
				}
					
				if (obj == null && nextAns == null)
				{
					throw new ServletException("Could not get any question rows!");
				}

					
			}
			
			// How many questions has this user been asked?
			obj.put("numQuestions", DbUtils.getNumQuestions(stmt, username));

			response.getWriter().write(obj.toString());

		    response.setContentType("application/json");
		    response.setHeader("Cache-Control", "no-cache");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ServletException(e);
		}
		finally
		{
			DbUtils.tryCloseConnection(conn);
		}

	}
	
	private String getNextAnswer(Statement stmt, String username) throws Exception
	{
		int ansNum = 0;
		String sql = "select * from users where username = '" + username + "'";
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) // this is not the user's first time
		{
			ansNum = rs.getInt("next_answer");
			sql = "update users set next_answer = " + (ansNum+1) + " where username = '" + username + "'";
			stmt.executeUpdate(sql);
		}
		else // this is the user's first time, so insert into Users table
		{
			ansNum = 0;
			sql = "insert into users(username, next_answer) values('" + username + "',1)";
			stmt.executeUpdate(sql);
		}
		
		// Read the special answers from the question file
		int numAnswers = QuestionFile.getAnswers().size();
		if (ansNum >= numAnswers)
		{
			// User has already gotten all the special answers
			return null;
		}
		else
		{
			// Get the next special answer for this user
			int index = ((ansNum + 1) * Constants.MODULO) % numAnswers; 
			index = (index + numAnswers) % numAnswers;
			return QuestionFile.getAnswers().get(index);
		}
		
		
	}
	
	/**
	 * Convert a question row to JSON.
	 */
	private JSONObject resultSetToJSON(ResultSet rs) throws Exception
	{
		if (rs.next())
		{
			JSONObject obj = new JSONObject();
			obj.put("body", rs.getString("body"));
			obj.put("questionID", rs.getInt("question_id"));
			obj.put("category", rs.getString("category"));
			obj.put("author", rs.getString("author"));
			obj.put("round", rs.getInt("round"));
			obj.put("tournament", rs.getString("tournament"));
			return obj;
		}
		return null;
	
	}
	
	/**
	 * Parse the options (eliminated tournaments or categories) passed in
	 * by the user, and set up the SQL WHERE clause accordingly.
	 */
	private String getOptions(HttpSession session)
	{
		Set<String> tours = (Set<String>)session.getAttribute("tournaments");
		Set<String> cats = (Set<String>)session.getAttribute("categories");
		String tourStr = DbUtils.createOptions("tournament", tours);
		String catStr = DbUtils.createOptions("category", cats);
		
		String sql = "where not(" + tourStr + ") and not(" + catStr + ") "; 
		return sql;
	}
	
	/**
	 * If the user has eliminated a tournament or category, add this information
	 * to the session. Or clear the eliminated tournaments and categories if
	 * the user clicked "clear".
	 */
	private void setUpOptions(HttpSession session, String tournament, String category,
			String reset)
	{
		if (session.getAttribute("tournaments") == null)
			session.setAttribute("tournaments", new HashSet<String>());
		
		if (session.getAttribute("categories") == null)
			session.setAttribute("categories", new HashSet<String>());
		
		Set<String> tours = (Set<String>)session.getAttribute("tournaments");
		Set<String> cats = (Set<String>)session.getAttribute("categories");
		
		if (tournament != null)
			tours.add(tournament);
		if (category != null)
			cats.add(category);
		if (reset != null)
		{
			tours.clear();
			cats.clear();
		}
			
	}
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

}
