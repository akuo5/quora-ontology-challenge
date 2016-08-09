import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Ontology {
	private static TopicNode<String> _topics;

	public static void makeTree(String flat) {
		System.out.println(flat);
		LinkedList<String> flatArray = new LinkedList<String>(Arrays.asList(flat.split(" ")));
		if (flatArray.size() >= 1) {
			_topics = makeTreeHelper(flatArray);
			_topics.printTree();
		} else {
			_topics = new TopicNode<String>(null);
		}
	}

	/**
	*	Will return a tree with a root and its children. The head node at arraySoFar should be
	*	the root of the tree node. arraySoFar should never start with "(" or ")" because it 
	*	should be caught in the previous call
	*/
	private static TopicNode<String> makeTreeHelper(LinkedList<String> arraySoFar) {
		TopicNode<String> treeRoot = new TopicNode<String>(arraySoFar.remove());
		
		// collect children
		if (arraySoFar.get(0).equals("(")) {
			arraySoFar.remove();
			while (!arraySoFar.get(0).equals(")")) {
				treeRoot.addChild(makeTreeHelper(arraySoFar));
			}
			arraySoFar.remove();
		}
		return treeRoot;
	}

	public static class TopicNode<T> {
	    private T data;
	    private ArrayList<TopicNode<T>> children;
	    private HashSet<T> allChildren;

	    public TopicNode(T rootData) {
	        data = rootData;
	        children = new ArrayList<TopicNode<T>>();
	        allChildren = new HashSet<T>();
	    }

	    public HashSet<T> getAllChildren() {
	    	return allChildren;
	    }

	    // whenever you add a child to this node, also add the child +
	    // it's children to the allChildren hashset
        public void addChild(TopicNode<T> child) {
        	children.add(child);
        	allChildren.add(child.data);
        	Iterator<T> iter = child.getAllChildren().iterator();
        	while (iter.hasNext()) {
        		allChildren.add(iter.next());
        	}
        }

        public boolean contains(String topic) {
        	return allChildren.contains(topic);
        }

	    public void printTree(){
	    	printTreeHelper(0);
	    }

	    public void printTreeHelper(int n) {
	    	for (int i = 0; i < n; i++) {
	    		System.out.print("  ");
	    	}
	    	System.out.println(data);
	    	for (int i = 0; i < children.size(); i++) {
				children.get(i).printTreeHelper(n+1);
			}
	    }

	}

    public static void main(String[] args) {
		BufferedReader reader = null;
		try {
		    File file = new File(args[0]);
		    reader = new BufferedReader(new FileReader(file));
		    // line 1			int N
		    int n = Integer.parseInt(reader.readLine());
		    System.out.println("N = "+n);
		    // line 2			flat tree of N topics
		    makeTree(reader.readLine());
		    // line 3			int M

		    // line 4..M+3		topic (colon): question text

		    // line M+4			int K

		    // line M+5..M+K+4	topic name (space) query text

		    
		    // reader.readLine();
		    System.out.println("------------");
		    String line;
		    while ((line = reader.readLine()) != null) {
		        System.out.println(line);
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
}

