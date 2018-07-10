import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Test;

public class AprioriTest {

	@Test
	public void test() {
		assertTrue((new LinkedList()).equals(new LinkedList()));
		Apriori a = new Apriori("transactions.txt",0.01);
		Queue<List<List<Integer>>> queue = a.resultItemSetsCollection;
		List<List<Integer>> l = queue.poll();
		List<HashSet<Integer>> result = new LinkedList<HashSet<Integer>>();
		for (List<Integer> list: l) {
			result.add(new HashSet<Integer>(list));
		}		
		assertTrue(a.getCsvData("OneItemSets.txt").equals(result));
		l = queue.poll();
		result = new LinkedList<HashSet<Integer>>();
		for (List<Integer> list: l) {
			result.add(new HashSet<Integer>(list));
		}			
		assertTrue(a.getCsvData("twoItemSets.txt").equals(result));	
		List<LinkedList<Integer>> threeItemSets = new LinkedList<LinkedList<Integer>>();
		LinkedList<Integer> set = new LinkedList<Integer>();
		set.add(39);
		set.add(704);
		set.add(825);
		threeItemSets.add(set);
		assertTrue(queue.poll().equals(threeItemSets));

	}

}
