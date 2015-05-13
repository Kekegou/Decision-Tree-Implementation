/*
 * This is a class for TreeNode definition
 * It contains attribute and constructor
 * 
 * @Author: Yangkelin Wei
 * @Date: 3/4/2015
 */
import java.util.ArrayList;


public class TreeNode {
	String attribute;// attribute of data
	String value;// value of attribute

	String realValue;// split point of attribute if attribute is real
	// children of parent node
	ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	boolean leaf;// whether the node is a leaf
	boolean leftChild;// whether the node is a left child of parent node

	TreeNode() {
		attribute = null;
		value = null;
		leaf = false;
		leftChild = false;

		realValue = null;
	}
}
