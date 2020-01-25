package org.nextprot.api.core.domain;

import com.google.common.collect.Sets;

import gnu.trove.set.TIntSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.service.CvTermGraphService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ActiveProfiles({"dev"})
public class CvTermGraphCircularReferenceTest extends CoreUnitBaseTest {

    @Autowired
    private TerminologyService terminologyService;

    @Autowired
    private CvTermGraphService cvTermGraphService;

    
    private Map<String,Integer> errors = new HashMap<>();
    private int currTermErrors=0;

    @Test
    public void shouldHaveNoCircularReferenceInAnyTerminologies() throws Exception {

    	for (TerminologyCv terminology: TerminologyCv.values()) {

    		getDuration();
            System.out.println("Loading terminology " + terminology.toString());
            CvTermGraph graph = cvTermGraphService.findCvTermGraph(terminology);
            System.out.println("Loaded graph in ms " + getDuration());
            currTermErrors=0;
            int[] nodes = graph.getNodes();
            int all = nodes.length;
            int idx = 0;
            for (int n:nodes) {
                Stack<Integer> stack = new Stack<>();
                getAncestors(n, stack, graph, false);
                if (idx % 1000 == 0) System.out.println("tested node " + idx + " / " + all);
            	idx++;
            }
            if (currTermErrors>0) errors.put(terminology.toString(), currTermErrors);
            System.out.println("tested " + idx + " in ms " + getDuration());
            System.out.println("----------------------");
    	}
    	
    	if (errors.size()==0 ) System.out.println("All terminologies are OK");
    	for (String k: errors.keySet()) System.out.println("ERRORs in " + k + ": " + errors.get(k));
        System.out.println("END");
        
        Assert.assertTrue(errors.size()==0);
        	
    }

    
    private void getAncestors(int node, Stack<Integer> stack, CvTermGraph graph, boolean printPaths) {
    	
    	// if the node is in the list we are circling
    	if (stack.contains(node)) {
    		stack.push(node);
    		System.out.println(stackToString(stack,graph) + "CIRCULAR REFERENCE");
    		currTermErrors++;
    		return;
    	}
    	
    	stack.push(node);
    	
    	int[] parents = graph.getParents(node);
        if (parents.length==0) {
        	if (printPaths) System.out.println(stackToString(stack,graph) + "STOP");
            return;
        }
        
        for (int parent: parents) {
        	getAncestors(parent, stack, graph, printPaths);
        	stack.pop();        	
        }
    }

    
    long t0 = 0;
    private long getDuration() {
    	if (t0 == 0) t0 = System.currentTimeMillis();
    	long duration = System.currentTimeMillis() - t0;
    	t0 = System.currentTimeMillis();
    	return duration;
    }
    
    private String stackToString(Stack<Integer> stack, CvTermGraph graph) {
    	StringBuilder s = new StringBuilder();
    	
    	for (int idx=0; idx<stack.size(); idx++) {
    		int n = stack.get(idx);
    		String rel = "";
    		if (idx + 1 <stack.size()) {
    			int n2 = stack.get(idx+1);
    			rel = graph.getEdgeLabel(n2, n);
    		}
    		s.append("" + n + " " + rel + " ");
    	}
    	return s.toString();
    }
    
    

}