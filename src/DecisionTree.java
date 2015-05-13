/*
 * This class mainly deals with data processing, and tree generation.
 * 
 * @Author: Yangkelin Wei
 * @Date: 3/4/2015
 *   
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class DecisionTree {
	// a set of the whole data set
	private static ArrayList<ArrayList<String>> set = new ArrayList<ArrayList<String>>();
	//all attribute types
	private static ArrayList<String> attribute_row = new ArrayList<String>();
	// all label types
	private static ArrayList<String> resultLabel = new ArrayList<String>();
	// specify real attribute
	private static String realAttribute = "";

	/*
	 * This function will read all data in file and categorize all information
	 * with different demands
	 * 
	 * @param trainFile: file name
	 * 
	 * @return the whole data set.
	 */
	public ArrayList<ArrayList<String>> readData(String trainFile) {
		Scanner scanner = null;

		try {

			scanner = new Scanner(new File(trainFile));

			boolean validData = false;
			boolean validLabel = false;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				ArrayList<String> subset = new ArrayList<String>();

				String[] curr = line.split(",");
				for (int i = 0; i < curr.length; i++) {

					subset.add(curr[i]);

				}
				// save attributes to attribute_row
				if (subset.get(0).contains("@attribute")) {
					String[] temp = subset.get(0).split("\\s");

					attribute_row.add(temp[1]);

				}

				if (subset.get(0).contains("real")) {

					String[] temp = subset.get(0).split("\\s");
					realAttribute += temp[1];

				}
				// save labels to resultLabel
				if (subset.get(0).contains("@attribute label")
						|| subset.get(0).contains("@attribute Label")) {

					validLabel = true;
				}

				// remove headings
				if (subset.get(0).contains("@data")) {
					validLabel = false;
				}

				if (validLabel && subset != null) {

					for (int i = 0; i < subset.size(); i++) {
						String temp = subset.get(i);
						if (temp.contains(String.valueOf("{"))) {
							resultLabel
									.add(temp.substring(temp.indexOf("{") + 1));

						} else if (temp.contains(String.valueOf("}"))) {
							resultLabel
									.add(temp.substring(0, temp.indexOf("}")));

						} else {
							if (temp != null) {
								resultLabel.add(temp);
							}

						}
					}
					if (resultLabel.get(resultLabel.size() - 1).equals(""))
						resultLabel.remove(resultLabel.size() - 1);
				}

				if (validData) {

					set.add(subset);

				}
				// remove headings
				if (subset.get(0).contains("@data")) {
					validData = true;
					validLabel = false;
				}

			}
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find the file");
		} catch (Exception e) {
			System.out.print(e);
		} finally {

			if (scanner != null)
				scanner.close();
		}

		return set;

	}

	/*
	 * This function will compose a hash map based on data set passed in The
	 * keys in hash map are attribute, and values are all corresponding value of
	 * that attribute
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @return a hash map.
	 */
	public HashMap<String, ArrayList<String>> composeMap(
			ArrayList<ArrayList<String>> remain_set) {
		HashMap<String, ArrayList<String>> attributeMap = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < attribute_row.size(); i++) {

			ArrayList<String> temp = new ArrayList<String>();
			for (int j = 0; j < remain_set.size(); j++) {

				temp.add(remain_set.get(j).get(i));
			}

			attributeMap.put(attribute_row.get(i), temp);

		}
		return attributeMap;
	}

	/*
	 * This function will compute entropy for discrete attribute
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @param attribute: a given specific attribute
	 * 
	 * @param value: a given specific value
	 * 
	 * @return entropy.
	 */
	public double computeEntropy(ArrayList<ArrayList<String>> remain_set,
			String attribute, String value) {
		int[] count = new int[resultLabel.size()];
		int index = 0;
		for (int i = 0; i < attribute_row.size(); i++) {
			if (attribute_row.get(i).equals(attribute)) {
				index = i;
				break;
			}
		}

		for (int j = 0; j < remain_set.size(); j++) {
			if (remain_set.get(j).get(index).equals(value)) {
				String temp = remain_set.get(j).get(attribute_row.size() - 1);
				for (int m = 0; m < resultLabel.size(); m++) {
					if (temp.equals(resultLabel.get(m))) {
						count[m]++;
					}
				}
			}

		}

		double sum = 0;
		for (int i = 0; i < count.length; i++) {
			sum += count[i];
		}
		double entropy = 0;
		if (sum == 0) {
			return 0;
		}
		for (int i = 0; i < count.length; i++) {
			if (count[i] == 0) {
				continue;
			}
			entropy += -count[i] / sum * Math.log(count[i] / sum)
					/ Math.log(2.0);
		}

		return entropy;
	}

	/*
	 * This function will compute entropy for real attribute
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @param attribute: a given specific attribute
	 * 
	 * @param splitPoint: a given specific splitPoint of continuous value
	 * 
	 * @param firstHalf: whether the value in remain_set is bigger or smaller
	 * than split point
	 * 
	 * @return entropy.
	 */
	public double computeEntropyReal(ArrayList<ArrayList<String>> remain_set,
			String attribute, double splitPoint, Boolean firstHalf) {
		int[] count = new int[resultLabel.size()];
		int index = 0;
		for (int i = 0; i < attribute_row.size(); i++) {
			if (attribute_row.get(i).equals(attribute)) {
				index = i;
				break;
			}
		}

		if (firstHalf) {
			for (int j = 0; j < remain_set.size(); j++) {
				if (Double.parseDouble(remain_set.get(j).get(index)) <= splitPoint) {
					String temp = remain_set.get(j).get(
							attribute_row.size() - 1);
					for (int m = 0; m < resultLabel.size(); m++) {
						if (temp.equals(resultLabel.get(m))) {
							count[m]++;
						}
					}
				}

			}
		} else {

			for (int j = 0; j < remain_set.size(); j++) {
				if (Double.parseDouble(remain_set.get(j).get(index)) > splitPoint) {
					String temp = remain_set.get(j).get(
							attribute_row.size() - 1);
					for (int m = 0; m < resultLabel.size(); m++) {
						if (temp.equals(resultLabel.get(m))) {
							count[m]++;
						}
					}
				}

			}

		}

		double sum = 0;
		for (int i = 0; i < count.length; i++) {
			sum += count[i];
		}

		double entropy = 0.0;
		if (sum == 0) {
			return 0;
		}
		for (int i = 0; i < count.length; i++) {
			if (count[i] == 0) {
				continue;
			}
			entropy += -count[i] / sum * Math.log(count[i] / sum)
					/ Math.log(2.0);
		}

		return entropy;

	}

	/*
	 * This function will compute parent entropy for both discrete and real
	 * attribute
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @param attribute: a given specific attribute
	 * 
	 * @return parent entropy
	 */
	public double computeParentEntropy(ArrayList<ArrayList<String>> remain_set,
			String attribute) {
		int[] count = new int[resultLabel.size()];
		for (int j = 0; j < remain_set.size(); j++) {
			String temp = remain_set.get(j).get(attribute_row.size() - 1);

			for (int m = 0; m < resultLabel.size(); m++) {

				if (temp.equals(resultLabel.get(m))) {
					count[m]++;
				}
			}
		}

		double sum = 0;
		for (int i = 0; i < count.length; i++) {
			sum += count[i];
		}

		double entropy = 0;
		if (sum == 0) {
			return 0;
		}
		for (int i = 0; i < count.length; i++) {

			if (count[i] == 0) {
				continue;
			}

			entropy += -(count[i] / sum * Math.log(count[i] / sum) / Math
					.log(2.0));

		}

		return entropy;
	}

	/*
	 * This function will compute split information for both discrete and
	 * attribute The purpose of computing split information is to get
	 * information gain rate
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @param attribute: a given specific attribute
	 * 
	 * @return split information.
	 */
	public double computeSplitInformation(
			ArrayList<ArrayList<String>> remain_set, String attribute) {

		double split = 0.0;

		ArrayList<String> tempList = composeMap(remain_set).get(attribute);// Si
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		ArrayList<String> typeCounter = new ArrayList<String>();//keys
		for (int i = 0; i < tempList.size(); i++) {
			if (tempMap.containsKey(tempList.get(i))) {
				tempMap.put(tempList.get(i), tempMap.get(tempList.get(i)) + 1);
			} else {
				tempMap.put(tempList.get(i), 1);
				typeCounter.add(tempList.get(i));
			}
		}

		for (int i = 0; i < typeCounter.size(); i++) {// split
			int specCounter = tempMap.get(typeCounter.get(i));
			if (specCounter == 0) {
				continue;
			}

			split += -specCounter / typeCounter.size()
					* Math.log(specCounter / typeCounter.size())
					/ Math.log(2.0);

		}

		return split;
	}

	/*
	 * This function will compute best split point for real attribute
	 * 
	 * @param values: sorted values of the real attribute
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @param attribute: the real attribute
	 * 
	 * @return best split point and max information gain
	 */
	public double[] getSplitPoint(ArrayList<String> values,
			ArrayList<ArrayList<String>> remain_set, String attribute) {
		double[] splitPoint = new double[values.size() - 1];

		double[] best = new double[2];
		double bestSplitPoint = 0.0;

		double gain = 0.0;

		double parent_entropy = computeParentEntropy(remain_set, attribute);

		for (int i = 0; i < values.size() - 1; i++) {
			splitPoint[i] = (Double.parseDouble(values.get(i)) + Double
					.parseDouble(values.get(i + 1))) / 2.0000;

		}

		double max = -100000.00;

		for (int i = 0; i < splitPoint.length; i++) {

			int[] count = new int[2];

			for (int j = 0; j < values.size(); j++) {
				if (Double.parseDouble(values.get(j)) <= splitPoint[i]) {
					count[0]++;

				} else {
					count[1]++;
				}
			}

			ArrayList<Double> ratioFactor = new ArrayList<Double>();
			for (int m = 0; m < 2; m++) {// S
				ratioFactor.add((double) count[m]
						/ (double) (count[0] + count[1]));

			}

			double children_entropy = 0.0;
			double temp = computeEntropyReal(remain_set, attribute,
					splitPoint[i], true);

			children_entropy += ratioFactor.get(0) * temp;

			temp = computeEntropyReal(remain_set, attribute, splitPoint[i],
					false);

			children_entropy += ratioFactor.get(1) * temp;

			gain = parent_entropy - children_entropy;

			gain = gain - (Math.log((splitPoint.length)) / Math.log(2.0))
					/ remain_set.size();

			if (max < gain) {
				max = gain;

				bestSplitPoint = splitPoint[i];

			}

		}
		best[0] = bestSplitPoint;
		best[1] = max;

		return best;

	}

	/*
	 * This function will compute information gain rate for both discrete and
	 * real attribute
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @param attribute: the real attribute
	 * 
	 * @return information gain rate
	 */
	public double computeGainRate(ArrayList<ArrayList<String>> remain_set,
			String attribute) {
		double gain = 0.0;
		double gainRate = 0.0;

		double parent_entropy = computeParentEntropy(remain_set, attribute);

		double children_entropy = 0.0;

		boolean realFlag = false;// this flag will notify whether the current
									// attribute is real or not
		if (realAttribute.contains(attribute)) {
			realFlag = true;
		}
		ArrayList<Integer> countValues = new ArrayList<Integer>();
		ArrayList<Double> ratioFactor = new ArrayList<Double>();

		ArrayList<String> values = composeMap(remain_set).get(attribute);

		if (!realFlag) {

			for (int i = 0; i < values.size(); i++) {// Sv
				int temp = 0;
				for (int j = 0; j < attribute_row.size(); j++) {
					if (attribute_row.get(j).equals(attribute)) {
						for (int m = 0; m < remain_set.size(); m++) {
							if (remain_set.get(m).get(j).equals(values.get(i))) {
								temp++;
							}
						}

					}
				}
				countValues.add(temp);
			}

			for (int i = 0; i < values.size(); i++) {// S
				ratioFactor.add((double) countValues.get(i)
						/ (double) remain_set.size());
			}

			for (int i = 0; i < values.size(); i++) {
				double temp = computeEntropy(remain_set, attribute,
						values.get(i));
				children_entropy += ratioFactor.get(i) * temp;
			}

			gain = parent_entropy - children_entropy;

			double split = computeSplitInformation(remain_set, attribute);

			gainRate = gain / split;
		}

		if (realFlag) {

			values = sort(values);

			double[] best = getSplitPoint(values, remain_set, attribute);

			double max = best[1];
			double splitPoint = best[0];

			double split = 0.0;
			ArrayList<String> tempList = composeMap(remain_set).get(attribute);// Si

			int[] count = new int[2];
			for (int i = 0; i < tempList.size(); i++) {

				if (Double.parseDouble(tempList.get(i)) <= splitPoint) {
					count[0]++;

				} else {
					count[1]++;
				}
			}

			double sum = count[0] + count[1];

			if (sum == 0) {
				split = 0;
			} else {

				split = -(count[0] / sum) * Math.log(count[0] / sum)
						/ Math.log(2.0) - (count[1] / sum)
						* Math.log(count[1] / sum) / Math.log(2.0);

				gainRate = max / split;

			}

		}

		return gainRate;

	}

	/*
	 * This function will judge whether all labels are the same as target label
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @param label: target label
	 * 
	 * @return all label same or not
	 */
	public boolean allLabelSame(ArrayList<ArrayList<String>> remain_set,
			String label) {
		int count = 0;
		for (int i = 0; i < remain_set.size(); i++) {
			if (remain_set.get(i).get(attribute_row.size() - 1).equals(label)) {
				count++;
			}
			if (count == remain_set.size()) {
				return true;
			}

		}
		return false;
	}
	
	/*
	 * This function will determine a dominant label in a given data set
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @return dominant label
	 */
	public String dominantLabel(ArrayList<ArrayList<String>> remain_set) {

		int[] count = new int[resultLabel.size()];
		for (int i = 0; i < remain_set.size(); i++) {
			for (int j = 0; j < count.length; j++) {
				if (remain_set.get(i).get(attribute_row.size() - 1)
						.equals(resultLabel.get(j))) {
					count[j]++;
				}
			}
		}
		int max = 0;
		for (int m = 1; m < count.length; m++) {
			if (count[m] > count[max]) {
				max = m;
			}
		}
		return resultLabel.get(max);
	}
	

	/*
	 * This function will build up the decision tree
	 * 
	 * @param root: last root(parent root) in recursion
	 * 
	 * @param remain_set: current remaining data set
	 * 
	 * @param remain_attribute: current remaining attribute
	 * 
	 * @return root of the tree
	 */
	public TreeNode buildTree(TreeNode root,
			ArrayList<ArrayList<String>> remain_set,
			ArrayList<String> remain_attribute) {

		if (root == null) {
			root = new TreeNode();
		}

		// Leaf?
		for (int i = 0; i < resultLabel.size(); i++) {
			if (allLabelSame(remain_set, resultLabel.get(i))) {
				root.attribute = resultLabel.get(i);
				root.leaf = true;
				return root;
			}
		}


		// pruning
		// if remaining data less than 4, stop recursion
		if (remain_set.size() < 4) {
			String label = dominantLabel(remain_set);
			root.attribute = label;
			root.leaf = true;
			return root;
		}

		// find next attribute

		double maxGainRate = 0.0;
		double tempRate;
		String selectedAttribute = null;
		for (int i = 0; i < remain_attribute.size(); i++) {
			tempRate = computeGainRate(remain_set, remain_attribute.get(i));

			if (tempRate > maxGainRate) {
				maxGainRate = tempRate;

				selectedAttribute = remain_attribute.get(i);
			}
		}

		if (maxGainRate == 0) {

			String label = dominantLabel(remain_set);
			root.attribute = label;
			root.leaf = true;
			return root;
		}

		root.attribute = selectedAttribute;

		// remove considered discrete attribute
		ArrayList<String> new_attribute = new ArrayList<String>();
		for (String attr : remain_attribute) {
			if (attr.equals(selectedAttribute)
					&& !realAttribute.contains(selectedAttribute)) {
				continue;
			}
			new_attribute.add(attr);
		}

		// divide
		boolean realFlag = false;// whether current attribute is real

		if (realAttribute.contains(selectedAttribute)) {

			realFlag = true;
		}

		int attributeIndex = 0;
		for (int i = 0; i < attribute_row.size(); i++) {
			if (attribute_row.get(i).equals(selectedAttribute)) {
				attributeIndex = i;
			}
		}

		if (!realFlag) {

			ArrayList<String> valueSet = composeMap(remain_set).get(
					selectedAttribute);
			ArrayList<String> values = new ArrayList<String>();
			String valueType = "";

			for (int m = 0; m < valueSet.size(); m++) {
				if (!valueType.contains(valueSet.get(m))) {
					valueType += valueSet.get(m);
					values.add(valueSet.get(m));
				}
			}
			// get all collections of attribute with current value
			for (int i = 0; i < values.size(); i++) {
				ArrayList<ArrayList<String>> new_set = new ArrayList<ArrayList<String>>();
				for (int j = 0; j < remain_set.size(); j++) {
					if (remain_set.get(j).get(attributeIndex)
							.equals(values.get(i))) {
						new_set.add(remain_set.get(j));
					}
				}

				TreeNode newNode = new TreeNode();
				newNode.attribute = selectedAttribute;
				newNode.value = values.get(i);
				// newNode.parentValue = values.get(i);

				// no data set satisfy current branch
				if (new_set.size() == 0) {
					newNode.attribute = dominantLabel(remain_set);
					newNode.value = dominantLabel(remain_set);
					newNode.leaf = true;
				} else {
					buildTree(newNode, new_set, new_attribute);// recursion

				}

				root.children.add(newNode);// add children to parent root

			}
		} else {
			ArrayList<String> values = composeMap(remain_set).get(
					selectedAttribute);

			values = sort(values);
			double[] best = getSplitPoint(values, remain_set, selectedAttribute);
			double bestSplitPoint = best[0];

			int counter = 2;// counter==2, left of split point, counter == 1,
							// right of split point
			while (counter > 0) {
				TreeNode newNode = new TreeNode();
				newNode.attribute = selectedAttribute;
				ArrayList<ArrayList<String>> new_set = new ArrayList<ArrayList<String>>();
				if (counter == 2) {

					for (int j = 0; j < remain_set.size(); j++) {
						if (Double.parseDouble(remain_set.get(j).get(
								attributeIndex)) <= bestSplitPoint) {

							new_set.add(remain_set.get(j));

						}
					}

					newNode.leftChild = true;
					root.realValue = (bestSplitPoint) + "";

				}
				double prev = bestSplitPoint;
				if (counter == 1) {

					for (int j = 0; j < remain_set.size(); j++) {
						if (Double.parseDouble(remain_set.get(j).get(
								attributeIndex)) > prev) {
							new_set.add(remain_set.get(j));

						}
					}

					root.realValue = (bestSplitPoint) + "";

				}
				counter--;

				if (new_set.size() == 0) {

					newNode.leaf = true;

					newNode.attribute = dominantLabel(remain_set);
					root.children.add(newNode);
					new_set = null;

				} else {

					buildTree(newNode, new_set, new_attribute);

					root.children.add(newNode);

					new_set = null;
				}
			}
		}

		return root;

	}

	/*
	 * This function will print the generated tree
	 * 
	 * @param root: the root of the tree
	 * 
	 * @return level order traversed tree
	 */
	public ArrayList<ArrayList<String>> print(TreeNode root) {
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		if (root == null) {
			return result;
		}
		queue.offer(root);

		while (!queue.isEmpty()) {
			ArrayList<String> level = new ArrayList<String>();
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				TreeNode head = queue.poll();
				if (head.value != null) {
					level.add(head.attribute + "   #   " + head.value + "   ");
				} else {
					level.add(head.attribute + "   #   " + head.realValue
							+ "   ");
				}

				for (int j = 0; j < head.children.size(); j++) {

					queue.offer(head.children.get(j));

				}

			}
			result.add(level);
		}
		return result;
	}



	/*
	 * This function will sort values for real attribute
	 * 
	 * @param values: unsorted values of the real attribute
	 * 
	 * @return sorted values
	 */
	public ArrayList<String> sort(ArrayList<String> values) {
		ArrayList<Double> temp = new ArrayList<Double>();
		for (String s : values) {
			temp.add(Double.parseDouble(s));
		}
		Collections.sort(temp);
		int i = 0;
		for (Double d : temp) {
			values.set(i, d + "");
			i++;
		}

		return values;
	}

	/*
	 * This function will call all necessary functions to build a tree
	 * 
	 * @param inputSet: data set needed in tree
	 * 
	 * @return root of tree
	 */
	public TreeNode startBuild(ArrayList<ArrayList<String>> inputSet) {

		TreeNode start = null;

		set = inputSet;

		TreeNode root = buildTree(start, set, attribute_row);
		ArrayList<ArrayList<String>> tree = print(root);
		// commented code can print out generated tree
		 for(int i = 0; i < tree.size(); i++){
		 System.out.println("@@@@@@@@@@@@@@@@@@@");
		 for(int j = 0; j < tree.get(i).size();j++){
		 System.out.print(tree.get(i).get(j));
		
		 }
		 System.out.println();
		 }

		return root;

	}

	public ArrayList<String> getAttributeRow() {
		return attribute_row;
	}

	public String getRealAttribute() {
		return realAttribute;
	}

}
