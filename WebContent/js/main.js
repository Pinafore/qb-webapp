var hasQuestion = false;
var isAnswering = false;
var hasAnswer = false;
var usernameFocused = false;

var questionID = 0;

// TOP-LEVEL FUNCTIONS
function newQuestion(text)
{
	hasQuestion = true;
	isAnswering = false;
	hasAnswer = false;
	showAndHide();
	$("#answerBox").val(""); // clear the answer box
	
	displayQuestion(text);
}

function startAnswer()
{
	stopDisplay();
	isAnswering = true;
	showAndHide();
	$("#answerBox").focus();
}

function submitAnswer()
{
	hasQuestion = false;
	isAnswering = false;
	showAndHide();
	displayWholeQuestion();
	
	submitAnswerAjax(false);
}

function giveUp()
{
	$("#answerBox").val("");
	if (!isAnswering)
		startAnswer();
	submitAnswer();
}

// FUNCTIONS FOR SETTING UP DISPLAY

function showAndHide()
{
	toggle($("#startAnswerButton"), hasQuestion && !isAnswering);
	toggle($("#submitAnswerButton"), isAnswering);
	toggle($("#giveUpButton"), hasQuestion);
	toggle($("#newQuestionButton"), !hasQuestion);
	toggle($("#responseMain"), hasAnswer);
	
	// TODO: bind the hotkeys
	
}

function toggle(item, value)
{
	if (value)
		item.show();
	else
		item.hide();
}


// AJAX FUNCTIONS

function getQuestion(data)
{
	if ($("#usernameBox").val() == "")
	{
		alert("You must enter your name first");
		$("#usernameBox").focus();
	}
	else
	{
		block(); // do SimpleModal
		var username = $("#usernameBox").val();
		data['username'] = username;
		$.ajax({
			  url: 'QuestionServlet',
			  dataType: 'json',
			  data: data,
			  success: getQuestionCallback,
			  error: questionError
			});	
	}
}

function getQuestionCallback(data)
{
	questionID = data.questionID;
	
	// populate the tournament, category, etc
	$("#category").text(data.category);
	$("#tournament").text(data.tournament);
	
	newQuestion(data.body);
	setNumQuestions(data.numQuestions);
}

function questionError(request,status,error)
{
	alert("No questions were found!\nDid you eliminate too many categories or tournaments?");
}

function submitAnswerAjax(isChanging)
{
	if ($("#usernameBox").val() == "")
	{
		alert("You must enter your name first");
		$("#usernameBox").focus();
	}
	else
	{
		var answer = $("#answerBox").val();
		var username = $("#usernameBox").val();
		var ajaxData = {
				questionID: questionID,
				username: username,
				answer: answer,
				isChanging: isChanging,
				numWords: displayedWords};
		
		$.ajax({
			  url: 'AnswerServlet',
			  dataType: 'json',
			  data: ajaxData,
			  success: submitAnswerCallback
			});
	}

}

function submitAnswerCallback(data)
{
	hasAnswer = true;
	showAndHide();
	
	var isBlank = ($("#answerBox").val() == "");
	
	toggle($("#orderArea"), data.correct);
	
	toggle($("#correct"), data.correct);
	toggle($("#incorrect"), !isBlank && !data.correct);
	toggle($("#blank"), isBlank);
	toggle($("#changeAnswerButton"), !isBlank && !data.correct);
	
	$("#answeredBefore").html(arrayToList(data.less));
	$("#answeredAfter").html(arrayToList(data.more));
	$("#correctList").html(arrayToList(data.correctAnswers));
	$("#incorrectList").html(arrayToList(data.incorrectAnswers));
	
	setNumQuestions(data.numQuestions);
}

function arrayToList(array)
{
	var list = "";
	var i = 0;
	for (i = 0; i < array.length; i++)
		list += "<p class='listEntry'>" + array[i] + "</p>";
	
	if (array.length == 0)
		list = "<p class='listEntry'>(None)</p>";
	return list;
}

// SIMPLEMODAL FUNCTIONS

function block()
{
	$('#simplemodal-container').height('50px');
	$.modal("<div id='message'><h1><img src='images/busy.gif' />Loading...</h1></div>", 
			{close:false});
	//$.blockUI({ message: '<h1><img src="busy.gif" /> Loading...</h1>' }); 
}

function unblock()
{
	$.modal.close();
}

// MISC FUNCTIONS


function startup()
{
	
	$("#slider").slider({
	      min:2,
	      max:20,
	      value:4,
	      change: function() {if (hasQuestion && !isAnswering) changeInterval();}
	});

	// add busy indicators
	$(document).ajaxStop(unblock);
	
	// add username focus handlers
	$("#usernameBox").focus(function() {usernameFocused = true;});
	$("#usernameBox").blur(function() {usernameFocused = false;});
	
	$(document.documentElement).keypress(keyPressed);
	$("#numQuestionsDiv").hide();
	showAndHide();
}

function keyPressed(event)
{
	if (event.which === 32 && hasQuestion && !isAnswering
			&& !usernameFocused) // space bar
	{
		event.preventDefault();
		startAnswer();
	}
	
	if (event.which === 32 && !hasQuestion && !usernameFocused)
	{
		event.preventDefault();
		getQuestion({});
	}
	
	if (event.which === 13 && isAnswering) // enter
	{
		event.preventDefault();
		submitAnswer();
	}
	
	if ((event.which === 83 || event.which === 115) && // 's' key
			hasQuestion && !isAnswering)
	{
		event.preventDefault();
		giveUp();
	}
		
}

function setNumQuestions(num)
{
	$("#numQuestions").html(num);
	$("#numQuestionsDiv").show();
}
