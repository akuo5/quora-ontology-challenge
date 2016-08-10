import java.io.*;
import java.util.*;

public class Ontology {
	private static TopicNode<String> _topics;
	private static HashMap<String, HashSet<String>> _map;
	private static Trie _trie;

	public static void makeTree(String flat) {
		LinkedList<String> flatArray = new LinkedList<String>(Arrays.asList(flat.split(" ")));
		if (flatArray.size() >= 1) {
			_topics = makeTreeHelper(flatArray);
		}
	}

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

		_map.put(treeRoot.data, treeRoot.allChildren);
		return treeRoot;
	}

	public static class TopicNode<T> {
	    private String data;
	    private HashSet<String> allChildren;

	    public TopicNode(String rootData) {
	        this.data = rootData;
	        this.allChildren = new HashSet<String>();
	    }

	    public HashSet<String> getAllChildren() {
	    	return allChildren;
	    }

        public void addChild(TopicNode<String> child) {
        	allChildren.add(child.data);
        	if (!child.getAllChildren().isEmpty()) {
        		Iterator<String> iter = child.getAllChildren().iterator();
	        	while (iter.hasNext()) {
	        		allChildren.add(iter.next());
	        	}
        	}
        }
	}

	private static class TrieNode {
		char val;
	    HashMap<Character, TrieNode> set;
	    HashMap<String, Integer> topics;

		public TrieNode() {
	        this.set = new HashMap<Character, TrieNode>();
	        this.topics = new HashMap<String, Integer>();
	    }

	    public TrieNode(char v) {
	    	this.val = v;
	        this.set = new HashMap<Character, TrieNode>();
	        this.topics = new HashMap<String, Integer>();
	    }

	    public void addTopic(String topic) {
	    	if (topics.containsKey(topic)) {
	    		int count = topics.get(topic);
	    		topics.put(topic, count+1);
	    	} else {
	    		topics.put(topic, 1);
	    	}
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

	            if(pointer.set.containsKey(c)){
	                pointer = pointer.set.get(c);
	            } else {
	                TrieNode t = new TrieNode(c);
	                pointer.set.put(c, t);
	                pointer = t;
	            }
	            pointer.addTopic(topic);
	        }
	    }
	 
	    // if text doesn't exist, return 0
	    public Integer numAssociations(String topic, String text) {
	    	HashSet<String> children = _map.get(topic);
	        TrieNode found = searchNode(text);
	        int count = 0;
	        if(found == null || children == null ) {
	            return count;
	        } else {
	            Iterator<String> iter = found.topics.keySet().iterator();
	            while (iter.hasNext()) {
	            	String check = iter.next();
	            	if (check.equals(topic) || children.contains(check)) {
	            		count+=found.topics.get(check);
	            	}
	            }
	            return count;
	        }
	    }

	    // returns the node based off the text, otherwise will return null
	    public TrieNode searchNode(String text){
	        TrieNode pointer = root;

	        for(int i = 0; i < text.length(); i++) {
	            char c = text.charAt(i);
	            pointer = pointer.set.get(c);

	            if (pointer == null) {
	                return pointer;
	            }
	        }
	        return pointer;
	    }
	}

	public static void main(String args[] ) throws Exception {
		long startTime = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    // line 1			int N
	    int n = Integer.parseInt(reader.readLine());
	    // line 2			flat tree of N topics
	    _map = new HashMap<String, HashSet<String>>();
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
		long endTime = System.currentTimeMillis();
		System.out.println("Took " + (endTime-startTime) + " milliseconds.");
    }
}

