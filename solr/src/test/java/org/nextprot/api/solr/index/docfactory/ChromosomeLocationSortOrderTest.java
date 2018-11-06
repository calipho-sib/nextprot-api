package org.nextprot.api.solr.index.docfactory;

import org.junit.Test;
import org.nextprot.api.solr.index.docfactory.entryfield.ChromosomeSolrFieldCollector;

import static org.junit.Assert.assertEquals;

public class ChromosomeLocationSortOrderTest {

	@Test
	public void test1() { computeAndCheckValueFor("19q13.32"); }

	@Test
	public void test2() { computeAndCheckValueFor("19q13.32 1q13.32"); }

	@Test
	public void test31() { computeAndCheckValueFor("2p21"); }

	@Test
	public void test32() { computeAndCheckValueFor("2p21,"); }

	@Test
	public void test33() { computeAndCheckValueFor("2p21, 19q13.32, 14q32.11"); }

	@Test
	public void test4() { computeAndCheckValueFor("9q13.3"); }

	@Test
	public void test5() { computeAndCheckValueFor("19q13"); }

	@Test
	public void testStringBuilder() { 
	
		StringBuilder sb = new StringBuilder();
		sb.append("123xx");
		int start = sb.length()-2;
		sb.delete(start, start+2);
		assertEquals("123", sb.toString());
	}
	

	
	public void computeAndCheckValueFor(String positions) {
		Integer actualValue = ChromosomeSolrFieldCollector.sortChr(positions);
		Integer expectedValue = new Long(sortChrOld(positions)).intValue();
		System.out.println("order value for " + positions + " : expected " + expectedValue + " - got " + actualValue);
		assertEquals(expectedValue, actualValue);
	}
	
	// method used in old implementation in nextmodel svn project
	public long sortChrOld(String chrs) {

		// base the computation of chr_loc_s on first chr_loc
		String chr = chrs.split(" ")[0];
		chr=chr.trim();
		System.out.println("old method: value of chr_loc before computation of chr_loc_s: " + chr);
		
		String[] chr_loc=chr.split("([pq]|cen)");  // split on p or q
		long f_chr0=1000000; 	 
		long f_q=50000; 	 
		long f_chr1=1000; 		
		int  max_chr=50;		// max chr localtion after pq 

		long chr0, chr1;

		
		// push unknown chromosome at the end 
		if (chr.indexOf("unknown")>-1 || chr.equals("")) {
			//System.out.println(f_chr0*30+  " = "+chr);
			return f_chr0*30;
		}
		if(chr_loc[0].equalsIgnoreCase("x")){
			chr0=23*f_chr0;
		}else if(chr_loc[0].equalsIgnoreCase("y")) {
			chr0=24*f_chr0;
		}else if(chr_loc[0].equalsIgnoreCase("mt")) {
			chr0=25*f_chr0;
		}else{
			chr0=Integer.parseInt(chr_loc[0])*f_chr0;
		}
		
		System.out.println("step1: chr0="+chr0);

		//
		// sort(cen) = 10E5*XX + 10E4-1
		if (chr.indexOf("cen")>-1){
			//System.out.println(chr+" = "+(chr0+f_q-1));
			return chr0+f_q-1;			
		}
		
		//
		// sort(chr) = 10E5*XX 
		if (chr_loc.length==1){
			//System.out.println(chr+" = "+(chr0));
			return (chr0);
		}
		
		// extract double value from digits after p or q
		chr1=(long)( Double.parseDouble(chr_loc[1].split("[-,]")[0]) * f_chr1);
		
		System.out.println("step2: chr1="+chr1);

		
		// sort(q) = 10E5*XX + 10E4 + 100*YY
		if(chr.indexOf('q')>-1){
			//System.out.println(chr+" = "+(chr0+chr1+f_q));
			System.out.println("step3: has q");			
			return chr0+chr1+f_q;			
		} 
		
		// sort(p) = 10E6*XX + 1000*(45-YY)  //descending order
		//System.out.println(chr+" = "+(chr0 + f_chr1 * max_chr - chr1));
		return chr0 + f_chr1 * max_chr - chr1;
	}

	
}
