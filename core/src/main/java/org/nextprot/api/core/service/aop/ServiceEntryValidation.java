package org.nextprot.api.core.service.aop;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.service.annotation.ValidEntry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Aspect used to validate a given entry. 
 * 
 * @author Daniel Teixeira
 * @version $Revision$, $Date$, $Author$
 */
@Aspect
public class ServiceEntryValidation {

	private static final Log LOGGER = LogFactory.getLog(ServiceEntryValidation.class);

	@Autowired
	private MasterIdentifierService masterIdentifierService;
	private Set<String> uniqueNames;

	private synchronized  Set<String> getUniqueNames(){
		if(uniqueNames == null){
			LOGGER.info("Loading neXtProt sequence unique names...");
			uniqueNames = new HashSet<>(masterIdentifierService.findUniqueNames());
		}
		return uniqueNames;
	}
	
	public ServiceEntryValidation() {

	}

	@Around("execution(* org.nextprot.api.*.service.*.*(..))")
	//@Around("execution(* org.nextprot.api.*.service.*.*(.., @aspects.ValidEntry (*), ..))")
	public Object checkValidEntry(ProceedingJoinPoint pjp) throws Throwable {

		Object[] arguments = pjp.getArgs();

		MethodSignature ms = (MethodSignature) pjp.getSignature();
		Annotation[][] annotations = ms.getMethod().getParameterAnnotations();

		int i = 0;
		for (Annotation[] paramAnnotations : annotations) {
			for (Annotation annotation : paramAnnotations) {
				if (ValidEntry.class.isAssignableFrom(annotation.getClass())) {
					if (!getUniqueNames().contains(arguments[i])) {
						LOGGER.error("neXtProt entry " + arguments[i] + " was not found, throwing EntryNotFoundException");
						throw new EntryNotFoundException((String) arguments[i]);
					}
					break;
				}

			}
			i++;
		}
		return pjp.proceed();

	}

}