package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.service.DbXrefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Lazy
@Service
public class DbXrefServiceImpl implements DbXrefService {
	@Autowired DbXrefDao dbXRefDao;
	
	private Set<String> dbXrefPropertyFilter;
	
	{
		this.dbXrefPropertyFilter = new HashSet<String>();
		this.dbXrefPropertyFilter.add("status");
		this.dbXrefPropertyFilter.add("match status");
		this.dbXrefPropertyFilter.add("organism ID");
		this.dbXrefPropertyFilter.add("organism name");
	}
	
	@Override
	public List<DbXref> findDbXRefByPublicationId(Long publicationId) {
		return this.dbXRefDao.findDbXRefsByPublicationId(publicationId);
	}
	
	@Override
	public List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds) {
		List<PublicationDbXref> xrefs = this.dbXRefDao.findDbXRefByPublicationIds(publicationIds);
		
		for(PublicationDbXref xref : xrefs)
			xref.setResolvedUrl(xref.resolveLinkTarget());

		return xrefs;
	}
	

	@Override
	public List<DbXref> findDbXRefByIds(List<Long> resourceIds) {
		List<DbXref> xrefs = this.dbXRefDao.findDbXRefByIds(resourceIds);
		return xrefs;
	}
	

	@Async
	@Override
	@Cacheable("xrefs")
	public List<DbXref> findDbXrefsByMaster(String uniqueName) {
		
		List<DbXref> xrefs = this.dbXRefDao.findDbXrefsByMaster(uniqueName);
		
		if(! xrefs.isEmpty())
			return getXrefProperties(xrefs, uniqueName);
		return xrefs;
	}
	
	public List<DbXref> findDbXrefsByEntry(String uniqueName) {
		return findDbXrefsByMaster(uniqueName);
	}

	/**
	 * 	private propertyNotPrinted = [
		'status',
		'match status',
		'organism ID',
		'organism name',
	];
	 * @param xrefs
	 * @return
	 */
	private List<DbXref> getXrefProperties(List<DbXref> xrefs, String uniqueName) {
		List<Long> xrefIds = Lists.transform(xrefs, new Function<DbXref, Long>() {
			public Long apply(DbXref xref) {
				return xref.getDbXrefId();
			}
		});

		List<DbXrefProperty> props = this.dbXRefDao.findDbXrefsProperties(xrefIds);
		
		Iterator<DbXrefProperty> it = props.iterator();
		
		while(it.hasNext()) {
			if(this.dbXrefPropertyFilter.contains(it.next().getName()))
				it.remove();
		}
				
		Multimap<Long, DbXrefProperty> propsMap = Multimaps.index(props, new Function<DbXrefProperty, Long>() {
			public Long apply(DbXrefProperty prop) {
				return prop.getDbXrefId();
			}
		});

		for (DbXref xref : xrefs) {
			xref.setProperties(new ArrayList<DbXrefProperty>(propsMap.get(xref.getDbXrefId())));
			xref.setResolvedUrl(resolveLinkTarget(uniqueName, xref));
		}
		
		return xrefs;
	}

	
	
	private String resolveLinkTarget(String primaryId, DbXref xref) {
		primaryId = primaryId.startsWith("NX_") ? primaryId.substring(3) : primaryId;
		if (! xref.getLinkUrl().contains("%u")) {
			return xref.resolveLinkTarget();
		}

		String templateURL = xref.getLinkUrl();
		if (!templateURL.startsWith("http")) {
			templateURL = "http://" + templateURL;
		}
		
		if (xref.getDatabaseName().equalsIgnoreCase("brenda")) {
			if (xref.getAccession().startsWith("BTO")) {
			    String accession = xref.getAccession().replace(":", "_");
				templateURL = CvDatabasePreferredLink.BRENDA_BTO.getLink().replace("%s", accession);
			}
			else {
				templateURL = templateURL.replaceFirst("%s1", xref.getAccession());
				String organismId = "247";
				// this.retrievePropertyByName("organism name").getPropertyValue();
				// organism always human: hardcode it
				templateURL = templateURL.replaceFirst("%s2", organismId);
			}
		}

		return templateURL.replaceAll("%u", primaryId);
	}




	@Override
	public List<DbXref> findDbXrefByAccession(String accession) {
		List<DbXref> xrefs = this.dbXRefDao.findDbXrefByAccession(accession);
		
//		for(DbXref xref : xrefs)
//			xref.setResolvedUrl(resolveLinkTarget(xref));

		return xrefs;	
	}

	@Override
	public List<DbXref> findAllDbXrefs() {
		List<DbXref> xrefs = this.dbXRefDao.findAllDbXrefs();
		
//		for(DbXref xref : xrefs)
//			xref.setResolvedUrl(resolveLinkTarget(xref));

		return xrefs;	
	}

	@Override
	public List<DbXref> findDbXRefByResourceId(Long resourceId) {
		return this.dbXRefDao.findDbXrefByResourceId(resourceId);
	}

	@Override
	public List<Long> getAllDbXrefsIds() {
		return this.dbXRefDao.getAllDbXrefsIds();
	}
	
}
