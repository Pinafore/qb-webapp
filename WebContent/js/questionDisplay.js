var totalWords = 0;
var displayedWords = 0;
var wordList = new Array();
var intervalID = 0;

// PUBLIC FUNCTIONS START HERE
function displayQuestion(text)
{
	wordList = text.split(" ");
	totalWords = wordList.length;
	if (totalWords <= 1)
	{
		alert("Question " + text + " didn't have enough words!");
	}
	displayedWords = 0;
	
	updateDisplay();
	
	var interval = 2000 / ($("#slider").slider("value"));
	intervalID =  setInterval("updateDisplay()", interval);
}

function stopDisplay()
{
	clearInterval(intervalID);
}

function changeInterval()
{
	clearInterval(intervalID);
	var interval = 2000 / ($("#slider").slider("value"));
	intervalID =  setInterval("updateDisplay()", interval);
}

function displayWholeQuestion()
{
	var str = getString(totalWords);
	$('#questionBox').text(str);
}

// PRIVATE FUNCTIONS START HERE
function updateDisplay()
{
	displayedWords++;
	if (displayedWords >= totalWords)
	{
		clearInterval(intervalID);
	}
	
	if (displayedWords <= totalWords)
	{
		var str = getString(displayedWords);
		$('#questionBox').text(str);	
	}
	
	// Scroll to bottom.
	// Code from: http://kisdigital.wordpress.com/2009/11/12/using-jquery-to-scroll-to-the-bottom-of-a-div/
	$("#questionBox").attr({ scrollTop: $("#questionBox").attr("scrollHeight") });
}

function getString(numWords)
{
	var i = 0;
	var str = "";
	for (i = 0; i < numWords; i++)
	{
		str = str + wordList[i] + " ";
	}
	return str;
}