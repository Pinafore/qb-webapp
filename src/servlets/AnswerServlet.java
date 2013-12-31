package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import util.Constants;
import util.DbUtils;
import util.Distance;

/**
 * This servlet is called when the user submits an answer, skips a 
 * question, or clicks "My answer was correct" for an answer judged as
 * incorrect. It judges the correctness of the answer and adds it to
 * the DB (if applicable), and sends back info such as the users who
 * answered before/after, etc.
 */
public class AnswerServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnswerServlet()
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
			String sql = "";
			PreparedStatement prep = null;

			int questionID = Integer.parseInt(request
					.getParameter("questionID"));
			String answer = request.getParameter("answer");
			int numWords = Integer.parseInt(request.getParameter("numWords"));
			String username = DbUtils.sanitize(request.getParameter("username"));

			// Did the user click "My answer was correct"?
			boolean isChanging = false;
			if (request.getParameter("isChanging") != null
					&& Boolean.parseBoolean(request.getParameter("isChanging")) == true)
			{
				isChanging = true;
			}

			boolean correct = false;

			JSONObject obj = new JSONObject();
			JSONArray lessArray = new JSONArray();
			JSONArray moreArray = new JSONArray();
			JSONArray correctArray = new JSONArray();
			JSONArray incorrectArray = new JSONArray();

			Set<String> correctSet = new TreeSet<String>();
			Set<String> incorrectSet = new TreeSet<String>();

			// If the user clicked "My answer was correct", change this answer
			// to correct
			if (isChanging)
			{
				sql = "update answers set correct = 1, is_reference = 1 " +
					"where question_id = ? and answer_text = ?";
				prep = conn.prepareStatement(sql);
				prep.setInt(1, questionID);
				prep.setString(2, answer);
				prep.execute();
			}

			// Get all the other answers for the same question
			sql = "select * from answers where question_id = " + questionID;
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next())
			{
				boolean otherCorrect = rs.getBoolean("correct");
				int otherNumWords = rs.getInt("num_words");
				String ansText = rs.getString("answer_text");
				String otherUsername = rs.getString("username");
				boolean isReference = rs.getBoolean("is_reference");

				String displayText = ansText;
				if (otherUsername.equals(Constants.DEFAULT_USERNAME))
					displayText += " (Judge Answer)";
				
				/* Add each correct, reference answer to the "Correct answers"
				 * list. Reference answers are judge answers and ones where the
				 * user clicked "My answer was correct".
				 */
				if (otherCorrect && isReference)
					correctSet.add(displayText.toLowerCase().trim());
				
				/*
				 * Add each incorrect answer to the "Incorrect answers" list.
				 */
				if (!otherCorrect)
					incorrectSet.add(displayText.toLowerCase().trim());

				if (otherCorrect)
				{
					String[] parts = ansText.split(" ");
					String lastPart = parts[parts.length-1];
					
					if (isReference && (isCorrect(answer, ansText) ||
							isCorrect(answer, lastPart)))
						correct = true;

					/*
					 * If this answer is correct, add the answerer's 
					 * username to the "answered before me" or "answered after
					 * me" list.
					 */
					if (!otherUsername.equals(Constants.DEFAULT_USERNAME) &&
						!otherUsername.equals(username))
					{
						if (otherNumWords < numWords)
							lessArray.put(otherUsername);
						else
							moreArray.put(otherUsername);
					}
				}
			}

			// write to the database, if we're submitting the initial answer
			// and it's not blank
			if (!isChanging && answer.length() > 0)
			{
				sql = "insert into answers(username, question_id, answer_date, "
						+ "answer_text, correct, num_words,is_reference) "
						+ "values(?,?,strftime('%s','now'),?,?,?,0)";
				prep = conn.prepareStatement(sql);
				prep.setString(1, username);
				prep.setInt(2, questionID);
				prep.setString(3, answer);
				prep.setInt(4, correct ? 1 : 0);
				prep.setInt(5, numWords);
				prep.execute();
			}

			// Create the JSON output
			for (String ans : correctSet)
				correctArray.put(ans);
			for (String ans : incorrectSet)
				incorrectArray.put(ans);
			
			obj.put("correct", correct);
			obj.put("less", lessArray);
			obj.put("more", moreArray);
			obj.put("correctAnswers", correctArray);
			obj.put("incorrectAnswers", incorrectArray);
			obj.put("numQuestions", DbUtils.getNumQuestions(stmt, username));

			System.out.println(obj.toString());
			response.getWriter().write(obj.toString());
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-cache");
		} catch (Exception e)
		{
			throw new ServletException(e);
		} finally
		{
			DbUtils.tryCloseConnection(conn);
		}
	}

	/**
	 * Use Levenshtein distance to determine whether these two answers
	 * match
	 */
	private boolean isCorrect(String myAnswer, String correctAnswer)
	{
		myAnswer = strip(myAnswer);
		correctAnswer = strip(correctAnswer);
		int distance = new Distance().LD(myAnswer, correctAnswer);
		return (distance <= Constants.MAX_DISTANCE && distance * 1.0
				/ correctAnswer.length() <= Constants.MAX_DISTANCE_PROP);
	}

	/**
	 * Remove all non alphanumeric characters from a string and set to lower case
	 */
	private String strip(String text)
	{
		text = text.replaceAll("[^a-zA-Z0-9]", "");
		return text.toLowerCase();

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
