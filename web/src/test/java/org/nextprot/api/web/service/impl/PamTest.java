package org.nextprot.api.web.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class PamTest {

	
/*
	The data required to run this "test" can be obtained by running the following queries:
	
	-- peptide - isoform mapping pairs
	
	set search_path=nextprot;
	select distinct pep.unique_name, x.accession, iso.unique_name from
	sequence_identifiers iso
	inner join mapping_annotations map on (Iso.identifier_id=map.reference_identifier_id)
	inner join sequence_identifiers pep on (map.mapped_identifier_id=pep.identifier_id) 
	inner join identifier_resource_assoc ira on (pep.identifier_id=ira.identifier_id)
	inner join db_xrefs x on (ira.resource_id=x.resource_id and x.cv_database_id=90)
	where iso.cv_type_id=2 and iso.cv_status_id=1
	and pep.cv_type_id=7 and pep.cv_status_id=1
	--and pep.unique_name='NX_PEPT02099902'
	--and iso.unique_name='NX_Q156A1-1'
	;
	
	-- isoform with same sequence
	
	select string_agg(iso.unique_name,',')	
	from sequence_identifiers iso
	inner join bio_sequences seq on (iso.identifier_id=seq.identifier_id)
	where iso.cv_type_id=2 and iso.cv_status_id=1
	group by seq.md5
	having count(*) > 1;
	 
	public static class SimpleBean {
	    private int x = 1;
	    private int y = 2;
//	    public Integer getX() {return x;}
//	    public Integer getY() {return y;}
	}

	@Test
	public void myTest1() throws Exception {

		
		ObjectMapper om = new XmlMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		SimpleBean b = new SimpleBean();
		String xml = om.writeValueAsString(b);
	    System.out.println(xml);
	    
		
		
	}
	
*/

	
	@Test
	public void myTest1() throws Exception {

		for (int i=1; i<100; i++) {
			int l2 = 32 - Integer.numberOfLeadingZeros(i);
		    System.out.println(i + " - " + l2);
		}
	    
		
		
	}

	
	
	Map<String,PPeptide> peptideMap;
	Set<Set<String>> equivIsoSets;
	Set<Set<String>> equivEntrySets;
	
	@Ignore
	@Test
	public void myTest() throws Exception {
		
		init();
		
		//PPeptide pep = peptideMap.get("PAp00003627"); setUnicity(pep); System.out.println(pep); 
		
		PrintWriter pw = new PrintWriter("/Users/pmichel/tmp/pseudo-unique/pep-unicity-report.csv", "UTF-8");
		peptideMap.values().stream().forEach(pep -> {
			setUnicity(pep);
			pw.println(pep);
		});
		pw.close();
		
	}
	
	public void setUnicity(PPeptide pep) {
		if (pep.mappedEntries.size()==1) {
			pep.unicity=PPeptide.Unicity.UNIQUE; // matches some isoform(s) of a single entry
		} else if (equivEntrySets.stream().anyMatch(s -> s.equals(pep.mappedEntries))) {
			List<Set<String>> includedSets = equivIsoSets.stream().filter(s -> pep.mappedIsoforms.containsAll(s)).collect(Collectors.toList());
			if (includedSets.size()!=1) throw new RuntimeException();
			pep.setEquivalentIsoforms(includedSets.get(0));
			pep.unicity=PPeptide.Unicity.PSEUDO_UNIQUE; // matches some isoforms of entries sharing an equivalent isoform
		} else {
			pep.unicity=PPeptide.Unicity.NON_UNIQUE; // matches isoforms of several entries not sharing any equivalent isoform 		
		}
	}
	
	
	
	public void init() throws Exception {
		String pepisoFile = "/Users/pmichel/tmp/pseudo-unique/pep-iso.csv";
		this.peptideMap = loadPepIsoFile(pepisoFile);
		String equivIsoFile = "/Users/pmichel/tmp/pseudo-unique/equiv-iso.csv";
		this.equivIsoSets = loadEquivIsoSets(equivIsoFile);
		this.equivEntrySets = loadEquivEntrySets(equivIsoFile);		
	}
	
	public Set<Set<String>> loadEquivIsoSets(String fileName) throws Exception {
		System.out.println("Opening " + fileName);
		BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
		Set<Set<String>> equivIsoSets = new HashSet<Set<String>>();
		while(true) {
			String line = in.readLine(); 
			if (line==null) break;
			String[] fields = line.split(","); // More than 1 fields expected
			Set<String> equiIsoSet = new TreeSet<>();
			for (int i=0;i<fields.length;i++) equiIsoSet.add(fields[i]);
			equivIsoSets.add(equiIsoSet);
		}
		in.close();
		System.out.println("loaded " + equivIsoSets.size() + " sets");
		System.out.println("Closed " + fileName);
		return equivIsoSets;
	}
	
	public Set<Set<String>> loadEquivEntrySets(String fileName) throws Exception {
		System.out.println("Opening " + fileName);
		BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
		Set<Set<String>> equivEntrySets = new HashSet<Set<String>>();
		while(true) {
			String line = in.readLine(); 
			if (line==null) break;
			String[] fields = line.split(","); // More than 1 fields expected
			Set<String> equiEntrySet = new TreeSet<>();
			for (int i=0;i<fields.length;i++) {
				String entry = fields[i].split("-")[0];
				equiEntrySet.add(entry);
			}
			equivEntrySets.add(equiEntrySet);
		}
		in.close();
		System.out.println("loaded " + equivEntrySets.size() + " sets");
		System.out.println("Closed " + fileName);
		return equivEntrySets;
	}
	
	public Map<String,PPeptide> loadPepIsoFile(String fileName) throws Exception {
		System.out.println("Opening " + fileName);
		BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
		Map<String,PPeptide> peptideMap = new TreeMap<String,PPeptide>();
		int lineNo=0;
		while(true) {
			lineNo++;
			if (lineNo % 100000 == 0) System.out.println("reading line " + lineNo + ", " + peptideMap.size() + " distinct peptides so far");
			
			String line = in.readLine(); 
			if (line==null) break;
			
			String[] fields = line.split("\\|"); // 3 fields expected
			String pepName=fields[0];
			String pepAc=fields[1];
			String isoName=fields[2];
			String entryName=isoName.split("-")[0];
		
			if (peptideMap.containsKey(pepAc)) {
				peptideMap.get(pepAc).addIsoEntry(isoName, entryName);
			} else {
				peptideMap.put(pepAc,new PPeptide(pepName,pepAc,isoName,entryName));
			}
			
		}
		in.close();
		System.out.println("" + peptideMap.size() + " distinct peptides loaded");
		System.out.println("Closed " + fileName);
		return peptideMap;
		
	}

	
	public static class PPeptide {
		public enum Unicity { UNIQUE, PSEUDO_UNIQUE, NON_UNIQUE }
		public String name;
		public String ac;
		public Unicity unicity;
		public Set<String> equivalentIsoforms;
		public Set<String> mappedIsoforms;
		public Set<String> mappedEntries;
		
		public PPeptide(String name, String ac, String iso, String entry) {
			this.name=name;
			this.ac=ac;
			this.mappedIsoforms=new TreeSet<String>();
			this.mappedIsoforms.add(iso);
			this.mappedEntries=new TreeSet<String>();
			this.mappedEntries.add(entry);
		}
		
		public void addIsoEntry(String iso, String entry) {
			this.mappedIsoforms.add(iso);
			this.mappedEntries.add(entry);
		}
		
		public void setEquivalentIsoforms(Set<String> isoSet) {
			this.equivalentIsoforms=isoSet;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(this.unicity).append("\t").append(this.name).append("\t").append(this.ac).append("\t");
			sb.append("maps ").append(mappedIsoforms).append("\t");
			if (equivalentIsoforms!=null) {
				String flag = mappedIsoforms.equals(equivalentIsoforms) ? "ALL" : "SOME";
				String msg = flag + " mapped isoforms are equivalent: " + equivalentIsoforms;
				sb.append(msg);
			}
			return sb.toString();
		}
	}
	
	
}
