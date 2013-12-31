package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import util.Constants;
import util.FileUtils;

public class BinaryFeatureCreator
{
	final static int MAX_ANS = 200;
	
	public static void main(String[] args) throws Exception
	{
		String inputFile = "C:/corpus/quizbowl/selected-questions-tokenized-small.txt";
		String outputFile = "C:/corpus/quizbowl/selected-questions-" + MAX_ANS + ".txt";
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		Map<String, List<String>> ansMap = new TreeMap<String, List<String>>();
		Map<String, List<String>> trainAnsMap = new TreeMap<String, List<String>>();
		
		Set<String> firstAns = getFirstAnswers();
		List<String> docs = new ArrayList<String>();
		List<String> trainDocs = new ArrayList<String>();
		
		List<String> lines = FileUtils.readAll(inputFile);
		for (int i = 0; i < lines.size(); i+= 2)
		{
			String quesLine = lines.get(i);
			String ansLine = lines.get(i+1).trim();
			
			// only add the "selected" answers!
			if (firstAns.contains(ansLine))
			{
				MapUtils.addToListMap(ansMap, ansLine, quesLine);
				docs.add(quesLine);
			}
		}
		
		int ansNum = 0;
		
		String trainStr = "";
		String devStr = "DEV\n";
		String testStr = "TEST\n";
		
		for (String answer : ansMap.keySet())
		{	
			//System.out.println(ansNum);
			List<String> quesList = ansMap.get(answer);
			List<String> trainQuesList = new ArrayList<String>();
			
			devStr += ansNum + " " + quesList.get(0) + "\n";
			testStr += ansNum + " " + quesList.get(1) + "\n";
			for (int i = 2; i < quesList.size(); i++)
			{
				trainStr += ansNum + " " + quesList.get(i) + "\n";
				trainDocs.add(quesList.get(i));
				trainQuesList.add(quesList.get(i));
			}
			trainAnsMap.put(answer, trainQuesList);
			ansNum++;
		}
		
		writer.write(trainStr);
		writer.write(devStr);
		writer.write(testStr);
		writer.close();
		
		Map<String, Integer> df = DfGenerator.generateDocFreqs(trainDocs);
		
		BuzzerWeightGenerator.generate(trainAnsMap);
	}
	
	private static String toBinary(String str)
	{
		return str;
	}
	
	private static Set<String> getFirstAnswers() throws Exception
	{
		List<String> lines = FileUtils.readAll(Constants.QUESTION_FILE_LOCATION);
		Set<String> ansSet = new HashSet<String>();
		for (int i = 0; i < MAX_ANS; i++)
		{
			int numAnswers = lines.size();
			int index = ((i + 1) * Constants.MODULO) % numAnswers;
			index = (index + numAnswers) % numAnswers;
			ansSet.add(StringUtils.fixString(lines.get(index)));
			System.out.println(StringUtils.fixString(lines.get(index)));
		}
		return ansSet;
	}
}
