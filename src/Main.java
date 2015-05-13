/*
 * This class contains a main method to start the whole program and will return the accuracy rate
 * 
 * @Author: Yangkelin Wei
 * @Date: 3/4/2015
 */
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		DecisionTree dt = new DecisionTree();
		CrossValidation cv = new CrossValidation();
		// data set file name
		ArrayList<ArrayList<String>> set = dt
			.readData(args[0]);
		
		// 10 means k = 10 in cross validation
		ArrayList<Double> result = cv.validation(set, 10);

		double accuracy = cv.calculateAccuracy(result, 10);

	
		System.out.println("The average accuracy is: "+accuracy * 100 + "%"); // result


	}

}
