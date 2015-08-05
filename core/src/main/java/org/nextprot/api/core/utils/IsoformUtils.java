package org.nextprot.api.core.utils;

import java.util.List;

import org.nextprot.api.core.domain.Isoform;


/**
 * Utils about isoforms
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class IsoformUtils {

	/**
	 * Gets the isoform by its name
	 * @param isoforms
	 * @param isoformName
	 * @return
	 */
	public static Isoform getIsoformByIsoName(List<Isoform> isoforms, String isoformName) {
		//TODO the isoforms should be stored in a map at the level of the Entry
		for(Isoform iso : isoforms){
			if(iso.getUniqueName().replaceAll("NX_", "").equals(isoformName.replaceAll("NX_", "")))
				return iso;	
		}
		return null;
	}
}
