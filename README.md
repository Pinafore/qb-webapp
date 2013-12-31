Quiz Bowl Web App Guide

***Setting up the project***

1) Create an SQLite database called questions.db and add questions to it, or use
an existing one (there's an example in the repo).

2) In the same directory as the DB, create questionFile.txt. This should be a
list of answers that the users must be asked about before they get asked
"random" questions. For example, if the line "George Washington" is in
questionFile.txt, a question will be asked that has judge answer "George
Washington". We used this for our experiments to ensure that some questions had
a lot of user responses. If you don't want to use this feature, just leave the
file blank.

3) Modify src/util/Constants.java to point to the directory containing
questions.db and questionFile.txt. Also modify the random seed for selecting
answers from questionFile.txt if you want.

4) In build.properties, change tomcat.lib to point to the Tomcat library
directory (should be the same as $CATALINA_HOME/lib). NOTE: We have been using
Tomcat 6; it's not clear whether will work for later versions.  If you get
compilation errors complaining about imports, you likely screwed this step up.

5) Compile and deploy!

***Compiling and Deploying***

How to compile the WAR file:

1) Clean out and replace any intermediate files:
         ant clean
2) Compile the package into a single, deployable file:
         ant package

How to replace the QuizBowl WAR file:

1) Stop Tomcat:
        $CATALINA_HOME/bin/shutdown.sh
2) Delete the old web app directory:
	rm -r $CATALINA_HOME/webapps/QuizBowl
3) Copy the new WAR file:
	cp QuizBowl.war $CATALINA_HOME/webapps/
4) Start Tomcat:
	$CATALINA_HOME/bin/startup.sh start

***Code Structure***

Contents of src directory:

- analysis: Classes for running statistical analysis of a SQLite DB. This was
  specific to the "proof-of-concept" demo we did a while back, and probably
  won't need to be used.

- commands: Classes for building and editing the DB

- servlets: Servlets called from the web app

- util: Utility classes

See Javadocs for more details of each class. In general, the "analysis" and
"commands" files have hard-coded file paths, so you will need to edit the code
to change them.
