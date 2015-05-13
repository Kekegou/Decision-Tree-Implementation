/*
 * This class mainly deals with cross validation of generated decision tree  
 * 
 * @Author: Yangkelin Wei
 * @Date: 3/4/2015
 * 
 */
import java.util.ArrayList;

public class CrossValidation {
	private ArrayList<ArrayList<ArrayList<String>>> shuffledSet = new ArrayList<ArrayList<ArrayList<String>>>();
	private ArrayList<ArrayList<String>> trainSet = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> testSet = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> recordSet = new ArrayList<ArrayList<String>>();
	private static ArrayList<String> attribute_row;
	private TreeNode root;
	private static String realAttribute;

	/*
	 * This function will shuffle data set into k folds. shuffle result would be different each
	 * time.
	 * 
	 * @param set: overall data set
	 * @param k: k fold cross validation
	 *
	 */
	
	public void shuffle(ArrayList<ArrayList<String>> set, int k) {
		int overallSize = set.size();
		int subSize = overallSize / k;

		for (int i = 0; i < k - 1; i++) {
			ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
			for (int j = 0; j < subSize; j++) {
				int index = (int) (Math.random() * (set.size()));
				temp.add(set.get(index));
				set.remove(index);
			}
			shuffledSet.add(temp);
		}

		ArrayList<ArrayList<String>> rest = new ArrayList<ArrayList<String>>();
		for (int m = 0; m < set.size(); m++) {
			rest.add(set.get(m));
		}

		shuffledSet.add(rest);

	}

	/*
	 * This function will implement test set to trained decision tree, and record result in
	 * recordSet
	 * 
	 *
	 */
	public void test() {

		for (int i = 0; i < testSet.size(); i++) {
			TreeNode node = root;
			ArrayList<String> curr = testSet.get(i);

			while (!node.leaf) {
				TreeNode pre = node;
				int attributeIndex = 0;
				for (int m = 0; m < attribute_row.size(); m++) {
					if (attribute_row.get(m).equals(node.attribute)) {

						attributeIndex = m;
					}
				}
				String value = curr.get(attributeIndex);

				boolean realFlag = false;

				if (realAttribute.contains(node.attribute)) {
					realFlag = true;
				}

				if (!realFlag) {
					ArrayList<TreeNode> children = node.children;

					for (TreeNode n : children) {

						if (n.value.equals(value)) {
							node = n;

						}
					}

				} else {
					ArrayList<TreeNode> children = node.children;

					if (Double.parseDouble(value) <= Double
							.parseDouble(node.realValue)) {
						for (TreeNode n : children) {
							if (n.leftChild) {
								node = n;
							}
						}
					} else {
						for (TreeNode n : children) {
							if (!n.leftChild) {
								node = n;
							}
						}
					}
				}
				if (node == pre) {
					break;
				}
			}
			recordSet.get(i).add(node.attribute);

		}

	}


	/*
	 * This function will call build tree method to train a tree
	 * 
	 * @param set: overall data set
	 * @param k: k fold cross validation
	 *
	 */

	public ArrayList<Double> validation(ArrayList<ArrayList<String>> set, int k) {

		shuffle(set, k);
		ArrayList<Double> result = new ArrayList<Double>();

		for (int i = 0; i < shuffledSet.size(); i++) {
			trainSet = new ArrayList<ArrayList<String>>();
			testSet = new ArrayList<ArrayList<String>>();
			recordSet = new ArrayList<ArrayList<String>>();

			for (int j = 0; j < shuffledSet.size(); j++) {
				if (i == j) {
					testSet.addAll(shuffledSet.get(j));
					recordSet.addAll(shuffledSet.get(j));
				} else {
					trainSet.addAll(shuffledSet.get(j));
				}
			}
			DecisionTree dt = new DecisionTree();

			root = dt.startBuild(trainSet);
			attribute_row = dt.getAttributeRow();
			realAttribute = dt.getRealAttribute();

			int count = 0;

			test();

			for (int m = 0; m < testSet.size(); m++) {
				if (recordSet.get(m).get(attribute_row.size())
						.equals(testSet.get(m).get(attribute_row.size() - 1))) {

					count++;
				}
			}

			result.add(count * 1.00 / testSet.size());

		}

		return result;

	}
	

	/*
	 * This function will calculate average rate
	 * 
	 * @param set: overall data set
	 * @param k: k fold cross validation
	 *
	 */
	public double calculateAccuracy(ArrayList<Double> result, int k) {
		double sum = 0.0;
		for (double d : result) {
			sum += d;
		}
		return (sum / k);
	}
}
