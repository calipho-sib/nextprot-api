package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;


@EntryFieldBuilder
public class PeptideFieldBuilder extends FieldBuilder{
	
	@Override
	protected void init(Entry entry){
		
		for(DbXref xf : entry.getXrefs()){
			if("PeptideAtlas".equals(xf.getDatabaseName()) || "SRMAtlas".equals(xf.getDatabaseName())){
				addField(Fields.PEPTIDE, xf.getDatabaseName() + ":" + xf.getAccession() + ", " + xf.getAccession());
			}
		}
				
	}
	
	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.PEPTIDE);
	}
	
}
