package org.nextprot.api.commons.velocity;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

/**
 * Implementation of velocity engine with caching enabled by default. If the
 * template was called, then the same teamplte is reused. This prevents memory
 * leaks according to:
 * http://velocity.apache.org/engine/devel/developer-guide.html : 
 * It is possible that your VelocityContext will appear to 'leak' memory (it is really just
 * gathering more introspection information.) What happens is that it
 * accumulates template node introspection information for each template it
 * visits, and as template caching is off, it appears to the VelocityContext
 * that it is visiting a new template each time. Hence it gathers more
 * introspection information and grows. It is highly recommended that you do one
 * or more of the following:
 * 
 * @author dteixeira
 *
 */
public class VelocityEngineWithTemplateCaching extends VelocityEngine {

	private Map<String, Template> templates = new HashMap<String, Template>();

	@Override
	public synchronized Template getTemplate(String templateName) {
		if (!templates.containsKey(templateName)) {
			Template t = this.getTemplate(templateName);
			templates.put(templateName, t);
		}
		return templates.get(templateName);
	}

}
