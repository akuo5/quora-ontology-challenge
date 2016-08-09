import java.io.*;
import java.util.*;

public class Ontology {
	private static TopicNode<String> _topics;
	private static HashMap<String, TopicNode<String>> _map;
	private static Trie _trie;

	public static void makeTree(String flat) {
		LinkedList<String> flatArray = new LinkedList<String>(Arrays.asList(flat.split(" ")));
		if (flatArray.size() >= 1) {
			_topics = makeTreeHelper(flatArray);
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
		_map.put(treeRoot.data, treeRoot);

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
	        this.data = rootData;
	        this.children = new ArrayList<TopicNode<T>>();
	        this.allChildren = new HashSet<T>();
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

	private static class TrieNode {
	    TrieNode[] array;
	    HashSet<String> topics;

	    public TrieNode() {
	    	// array[27] ==> ' '
	    	// array[28] ==> '?'
	        this.array = new TrieNode[57];
	        this.topics = new HashSet<String>();
	    }

	    public void addTopic(String topic) {
	    	topics.add(topic);
	    }
	}

	public static class Trie {
	    private TrieNode root;
	 
	    public Trie() {
	        root = new TrieNode();
	    }
	 	
	 	// inserts the word and associates each character with given topic
	    public void insertWord(String topic, String word) {
	        TrieNode pointer = root;

	        for(int i = 0; i < word.length(); i++){
	            char c = word.charAt(i);
	            int index = c - 'A';
	            if (c == ' ') { index = 27; }
	            if (c == '?') { index = 28; }
	            if(pointer.array[index] == null){
	                TrieNode temp = new TrieNode();
	                pointer.array[index] = temp;
	                pointer = temp;
	            } else {
	                pointer = pointer.array[index];
	            }
	            pointer.addTopic(topic);
	        }
	    }
	 
	    // if text doesn't exist, return 0
	    public Integer numAssociations(String topic, String text) {
	    	TopicNode node = _map.get(topic);
	        TrieNode found = searchNode(text);
	        int count = 0;
	        if(found == null){
	            return count;
	        } else {
	            Iterator<String> iter = found.topics.iterator();
	            while (iter.hasNext()) {
	            	String check = iter.next();
	            	if (check.equals(node.data) || node.contains(check)) {
	            		count++;
	            	}
	            }
	            return count;
	        }
	    }

	    // returns the node based off the text, otherwise will return null
	    public TrieNode searchNode(String text){
	        TrieNode pointer = root;
	        for(int i=0; i < text.length(); i++){
	            char c = text.charAt(i);
	            int index = c-'A';
	            if (c == ' ') { index = 27; }
	            if (c == '?') { index = 28; }
	            if(pointer.array[index]!=null){
	                pointer = pointer.array[index];
	            }else{
	                return null;
	            }
	        }
	        if(pointer == root)
	            return null;
	        return pointer;
	    }
	}

	public static void main(String args[] ) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    // line 1			int N
	    int n = Integer.parseInt(reader.readLine());
	    // System.out.println("N = "+n);
	    // line 2			flat tree of N topics
	    _map = new HashMap<String, TopicNode<String>>();
	    makeTree(reader.readLine());
	    // _topics.printTree();
	    // line 3			int M
	    int m = Integer.parseInt(reader.readLine());
	    // line 4..M+3		topic (colon): question text
	    _trie = new Trie();
	    while (m > 0) {
	    	String[] topicQuestion = reader.readLine().split(": ");
	    	_trie.insertWord(topicQuestion[0], topicQuestion[1]);
	    	m--;
	    }
	    // line M+4			int K
	    int k = Integer.parseInt(reader.readLine());
	    // line M+5..M+K+4	topic name (space) query text
	    while (k > 0) {
	    	String[] topicQuery = reader.readLine().split(" ", 2);
	    	System.out.println(_trie.numAssociations(topicQuery[0], topicQuery[1]));
	    	k--;
	    }
    }
}

