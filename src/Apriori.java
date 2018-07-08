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

	public Queue<List<List<Integer>>> itemSets;
	
	public Apriori(String filename, double support) {
		itemSets = new LinkedList<List<List<Integer>>>();
		LinkedList<HashSet<Integer>> transactions = getCsvData(filename);        
        int numbOfTrans = transactions.size();
        
        HashMap<Integer,Integer> oneItemSets = new HashMap<Integer,Integer>();
        HashSet<Integer> frequentOneItemSets = new HashSet<Integer>();
        
        for (HashSet<Integer> items: transactions) {
        	for (int i : items) {
        		Integer integer;
        		if ((integer = oneItemSets.get(i)) != null) {
        			oneItemSets.put(i, ++integer);
        		} else {
        			oneItemSets.put(i, 1);
        		}	
        	}
        }
        
        for (Entry<Integer,Integer> entry : oneItemSets.entrySet()) {
        	if (entry.getValue() >= numbOfTrans * support) {
        		frequentOneItemSets.add(entry.getKey());
        	}
        }
        
//        HashSet<Integer> tst = new HashSet<Integer>();
//        
//        tst.add(1);
//        tst.add(3);
//        tst.add(5);
//        System.out.println("tst"+generateSubsets(tst,2));
//        System.out.println();
//        HashSet<Integer> tst2 = new HashSet<Integer>();
//        
//        tst2.add(1);
//        tst2.add(3);
//        tst2.add(5);
//        System.out.println("tst 21212"+tst.equals(tst2));       
        
        HashSet<Integer> frequentItemSets = frequentOneItemSets;
        
        int currentCard = 1;
        HashSet<HashSet<Integer>> nextItemSets;
        while (!(nextItemSets = generateFrequentSubsets(frequentItemSets,transactions,currentCard,0.01)).isEmpty()) {
        	int size = nextItemSets.size();
        	System.out.println("There "+((size == 1)?"is":"are")+" exactly "+size+" frequent itemset"+((size == 1)?"":"s")+" containing "+currentCard+" item"+((currentCard == 1)?"":"s"));
        	frequentItemSets = collapseHashSet(nextItemSets);
        	System.out.println(sortHashSetOfHashSets(nextItemSets));
        	itemSets.add(sortHashSetOfHashSets(nextItemSets));
        	currentCard++;
        };
       
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


	private HashSet<HashSet<Integer>> generateFrequentSubsets(HashSet<Integer> set, LinkedList<HashSet<Integer>> transactions, int cardinality, double support) {
		HashSet<HashSet<Integer>> allSubsets = generateSubsets(set, cardinality);
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


	private HashSet<HashSet<Integer>> generateSubsets(HashSet<Integer> set, int cardinality) {
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		if (cardinality == 1) {
			for (int i: set) {
				HashSet<Integer> entry = new HashSet<Integer>();
				entry.add(i);
				result.add(entry);
			}
			return result; 
		}
		HashSet<HashSet<Integer>> recurse = generateSubsets(set, cardinality - 1);
		for (int setItem: set) {
			for (HashSet<Integer> items: recurse) {	
				HashSet<Integer> entry = new HashSet<Integer>(items);
				entry.add(setItem);
				if (entry.size() == cardinality) {
					result.add(entry);
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
	}
}
