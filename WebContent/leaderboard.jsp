<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="util.*" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Quiz Bowl Tester Leaderboard</title>
<link rel="stylesheet" type="text/css" href="main.css" />
</head>
<body>
<div id="main">
<a href="index.html">Return to Main Page</a>
	<table class="leaderTable">
	<tr class="leaderRow">
		<td class="leaderHeader">Username</td>
		<td class="leaderHeader">Total answers</td>
		<td class="leaderHeader">Correct answers</td>
		<td class="leaderHeader">Average position of answer</td>
	</tr>
	<%
		List<LeaderBoardEntry> entries = LeaderBoardEntry.getLeaderBoard();
		for (LeaderBoardEntry entry : entries)
		{
			out.println("<tr class='leaderRow'>");
			out.println("<td class='leaderCell'>" + entry.username + "</td>");
			out.println("<td class='leaderCell'>" + entry.numAnswers + "</td>");
			out.println("<td class='leaderCell'>" + entry.numCorrect + "</td>");
			out.println("<td class='leaderCell'>" + entry.avgWords + "</td>");
			out.println("</tr>");
		}
	%>
	</table>
</div>
</body>
</html>