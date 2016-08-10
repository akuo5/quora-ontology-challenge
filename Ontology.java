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

		_map.put(treeRoot.data, treeRoot);
		return treeRoot;
	}

	public static class TopicNode<String> {
	    String data;
	    HashSet<TopicNode<String>> parents;
	    HashSet<TopicNode<String>> children;

	    public TopicNode(String rootData) {
	        this.data = rootData;
	        this.parents = new HashSet<TopicNode<String>>();
	        this.children = new HashSet<TopicNode<String>>();
	    }

	    public void addParent(TopicNode<String> p) {
	    	parents.add(p);
	    }

        public void addChild(TopicNode<String> child) {
			children.add(child);
        	Iterator<TopicNode<String>> iter = child.children.iterator();
        	while (iter.hasNext()) {
        		children.add(iter.next());
        	}

        	addChildHelper(this, child);
        }

        public void addChildHelper(TopicNode<String> parent, TopicNode<String> child) {
        	child.addParent(parent);
        	Iterator<TopicNode<String>> iter = child.children.iterator();
        	while (iter.hasNext()) {
        		addChildHelper(parent, iter.next());
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
	    	TopicNode<String> t = _map.get(topic);
	    	Iterator<TopicNode<String>> parentsIter = t.parents.iterator();
	    	while (parentsIter.hasNext()) {
	    		TopicNode<String> p = parentsIter.next();
		    	incrementTopic(p.data);
	    	}
	    	incrementTopic(topic);
	    }

	    public void incrementTopic(String topic) {
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
	 
	    public Integer numAssociations(String topic, String text) {
	        TrieNode found = searchNode(text);
	        if(found == null || found.topics.get(topic) == null) {
	            return 0;
	        } else {
	        	return found.topics.get(topic);
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
		// long startTime = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    // line 1			int N
	    int n = Integer.parseInt(reader.readLine());
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
		// long endTime = System.currentTimeMillis();
		// System.out.println("Took " + (endTime-startTime) + " milliseconds.");
    }
}

