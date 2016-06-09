package org.nextprot.api.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PropagatorTest {

    @Test
    public void BasicTest() {
    	
    	// represent two coding regions of gene / master mapped to an isoform sequence
    	Map<Integer,Integer> map = new TreeMap<Integer,Integer>();
    	map.put(100, 104);
    	map.put(201, 204);
    	List<Map.Entry<Integer,Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(map.entrySet());

    	List<Integer> result;
    	
    	// get gene position of each nucl of the codon coding for aa of isoform at pos 1
    	result = Propagator.getGeneCodonPositions(1, list);
    	assertEquals(new Integer(100), result.get(0));
    	assertEquals(new Integer(101), result.get(1));
    	assertEquals(new Integer(102), result.get(2));

    	// get gene position of each nucl of the codon coding for aa of isoform at pos 2
    	result = Propagator.getGeneCodonPositions(2, list);
    	assertEquals(new Integer(103), result.get(0));
    	assertEquals(new Integer(104), result.get(1));
    	assertEquals(new Integer(201), result.get(2));

    	// get gene position of each nucl of the codon coding for aa of isoform at pos 3
    	result = Propagator.getGeneCodonPositions(3, list);
    	assertEquals(new Integer(202), result.get(0));
    	assertEquals(new Integer(203), result.get(1));
    	assertEquals(new Integer(204), result.get(2));
    	
       	// get gene position of each nucl of the codon coding for aa of isoform at pos 0 (out of range)
    	result = Propagator.getGeneCodonPositions(0, list);
    	assertEquals(0, result.size());
    	
       	// get gene position of each nucl of the codon coding for aa of isoform at pos 4 (out of range)
    	result = Propagator.getGeneCodonPositions(4, list);
    	assertEquals(0, result.size());
    	
    }
	
}
