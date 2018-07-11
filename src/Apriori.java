import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

public class Apriori {

	public Queue<List<List<Integer>>> resultItemSetsCollection;
	private LinkedList<HashSet<Integer>> transactions;
	double support;
	
	public Apriori(String filename, double support) {
		resultItemSetsCollection = new LinkedList<List<List<Integer>>>();
		transactions = getCsvData(filename); 
		this.support = support;	
	}

	private void run() {
		int currentCard = 1;
        HashSet<HashSet<Integer>> nextItemSets = getFrequentItems();
        printInfo(nextItemSets,currentCard);
        currentCard++;
        while (!(nextItemSets = generateFrequentSubsets(nextItemSets)).isEmpty()) {
        	printInfo(nextItemSets,currentCard);
        	resultItemSetsCollection.add(sortHashSetOfHashSets(nextItemSets));
        	currentCard++;
        };
	}
	
	private void printInfo(HashSet<HashSet<Integer>> nextItemSets, int currentCard) {
		int size = nextItemSets.size();
		System.out.println("There "+((size == 1)?"is":"are")+" exactly "+size+" frequent itemset"+((size == 1)?"":"s")+" containing "+currentCard+" item"+((currentCard == 1)?"":"s"));
    	System.out.println(sortHashSetOfHashSets(nextItemSets));
		
	}

	private HashSet<HashSet<Integer>> getFrequentItems() {
		HashSet<HashSet<Integer>> frequentOneItemSets = new HashSet<HashSet<Integer>>();
        HashMap<Integer,Integer> oneItemFrequencyMap = new HashMap<Integer,Integer>();
        for (HashSet<Integer> items: transactions) {
        	for (int i : items) {
        		Integer integer;
        		if ((integer = oneItemFrequencyMap.get(i)) != null) {
        			oneItemFrequencyMap.put(i, ++integer);
        		} else {
        			oneItemFrequencyMap.put(i, 1);
        		}	
        	}
        }
        int numbOfTrans = transactions.size();
        for (Entry<Integer,Integer> entry : oneItemFrequencyMap.entrySet()) {
        	if (entry.getValue() >= numbOfTrans * support) {
        		HashSet<Integer> innerResult = new HashSet<Integer>();
        		innerResult.add(entry.getKey());
        		frequentOneItemSets.add(innerResult);
        	}
        }
        return frequentOneItemSets;
	}
	
	private List<List<Integer>> sortHashSetOfHashSets(HashSet<HashSet<Integer>> setOfSets) {
		List<List<Integer>> sortedSets = new ArrayList<List<Integer>>(setOfSets.size()); 
    	for (HashSet<Integer> set: setOfSets){
    		List<Integer> sortedSet = new ArrayList<Integer>(set);
    		Collections.sort(sortedSet);
    		sortedSets.add(sortedSet);
    	}
    	Collections.sort(sortedSets, new ListComparator());	
		return sortedSets;	
	}
	
	private HashSet<Integer> collapseHashSet(HashSet<HashSet<Integer>> itemSets) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (HashSet<Integer> itemSet: itemSets) {
			result.addAll(itemSet);
		}
		return result;
	}


	private HashSet<HashSet<Integer>> generateFrequentSubsets(HashSet<HashSet<Integer>> set) {
		HashSet<HashSet<Integer>> allSubsets = generateSubsets(set);
		HashSet<HashSet<Integer>> results = new HashSet<HashSet<Integer>>();
		int size = transactions.size();
		for (HashSet<Integer> subset: allSubsets) {
			int count = 0;
			for (HashSet<Integer> transaction: transactions) {
				if (transaction.containsAll(subset)) {
					count++;
				}
			}
			if (count >= size * support) {
				results.add(subset);
			}
		}		
		return results;
	}


	public LinkedList<HashSet<Integer>> getTransactions() {
		return transactions;
	}

	public void setTransactions(String filename) {
		this.transactions = getCsvData(filename);
	}

	public double getSupport() {
		return support;
	}

	public void setSupport(double support) {
		this.support = support;
	}

	private HashSet<HashSet<Integer>> generateSubsets(HashSet<HashSet<Integer>> sets) {
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		int card = 0;
		for (HashSet<Integer> set: sets) {
			card = set.size(); 
			for (HashSet<Integer> set2: sets) {
				HashSet<Integer> innerResult = new HashSet<Integer>(set);
				innerResult.addAll(set2);
				if (innerResult.size() == card + 1) {
					result.add(innerResult);
				}
			}
			
		}
		return result;
	}
	
    public LinkedList<HashSet<Integer>> getCsvData(String filename) {
	 	BufferedReader br = null;
        String line = "";
        String splitBy = " ";
        LinkedList<HashSet<Integer>> transactions = new LinkedList<HashSet<Integer>>();
        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
            	String[] strArr = line.split(splitBy);
            	HashSet<Integer> intSet = new HashSet<Integer>();
            	for (int i = 0; i<strArr.length; i++ ){
            		intSet.add(Integer.parseInt(strArr[i]));
            	}
            	transactions.add(intSet);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return transactions;
    }

	
	public static void main(String[] args) {
		Apriori apr = new Apriori("transactions.txt", 0.01);
		apr.run();
		apr.setTransactions("transactionslarge.txt");
		apr.run();		
	}
}
