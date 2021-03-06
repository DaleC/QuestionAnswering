package qa.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qa.Application;
import qa.model.QueryTerm;
import qa.model.QueryTermImpl;
import qa.model.QuestionInfo;
import qa.model.QuestionInfoImpl;
import qa.model.enumerator.QueryType;

public class QuestionClassifierHelper {

	private static QuestionClassifierHelper instance;
	private final String re1 = "((?:[a-z][a-z]+))"; // Word 1
	private final String re2 = ":"; // Non-greedy match on filler
	private final String re3 = "((?:[a-z][a-z]+))"; // Word 2
	private final String re4 = " "; // Non-greedy match on filler
	private final String re5 = "((?:.*))"; // Variable Name 1

	private final Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private QuestionClassifierHelper() {

	}

	public static QuestionClassifierHelper getInstance() {
		if (instance == null) {
			instance = new QuestionClassifierHelper();
		}

		return instance;
	}

	public List<QuestionInfo> getTrainingData(String corpusPath) {
		List<QuestionInfo> trainingData = new ArrayList<QuestionInfo>();
		File folder = new File(corpusPath);
		File[] fileList = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(
						Application.Settings.getProperty(
								"CLASSIFIER_TRAINING_EXT").toLowerCase())
						&& name.toLowerCase().startsWith(
								Application.Settings.getProperty(
										"CLASSIFIER_TRAINING_PREFIX")
										.toLowerCase());
			}
		});
		for (File file : fileList) {
			int questionCount = 0;
			BufferedReader br = null;
			try {
				String line;
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {
					questionCount++;
					trainingData.add(getQuestionInfo(line));
				}

				System.out.printf("Training set: %s [ %d questions]\n",
						file.getName(), questionCount);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return trainingData;
	}

	private QuestionInfo getQuestionInfo(String text) {
		Matcher m = p.matcher(text);
		if (m.find()) {
			String queryType = m.group(1);
			// String subQueryType = m.group(2);
			String rawQuestion = m.group(3);
			List<QueryTerm> terms = getQueryTerms(rawQuestion);
			QuestionInfo questionInfo = new QuestionInfoImpl(
					QueryType.valueOf(queryType), terms);
			// System.out.println(questionInfo);
			return questionInfo;
		} else {
			return null;
		}
	}

	public List<QueryTerm> getQueryTerms(String text) {
		List<QueryTerm> terms = new ArrayList<QueryTerm>();
		Pattern wordPattern = Pattern.compile("\\w+", Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		Matcher m = wordPattern.matcher(text);
		while (m.find()) {
			terms.add(new QueryTermImpl(m.group()));
		}

		return terms;
	}

	public List<QueryType> getAllQueryTypes() {
		return Arrays.asList(QueryType.class.getEnumConstants());
	}
}
