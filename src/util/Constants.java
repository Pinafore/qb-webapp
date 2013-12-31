package util;

/**
 * Important constants used in the web app.
 */
public class Constants
{
	// Location containing questions.db and questionFile.txt
	private final static String BASE_LOCATION = //"C:/corpus";
		"/scratch";

	public final static String DB_LOCATION = BASE_LOCATION + "/questions.db";
	public final static String QUESTION_FILE_LOCATION = BASE_LOCATION +  "/questionFile.txt";

	// Parameters for Levenshtein distance. Two answers are considered the same if their
	// distance <= MAX_DISTANCE, AND distance <= (MAX_DISTANCE_PROP * length of correct answer).
	public final static int MAX_DISTANCE = 3;
	public final static double MAX_DISTANCE_PROP = 0.2;

	// Modulo used when choosing the order of special answers from questionFile.txt
	public final static int MODULO = 10001;

	// Username used for judge answers
	public final static String DEFAULT_USERNAME = "default";
}
