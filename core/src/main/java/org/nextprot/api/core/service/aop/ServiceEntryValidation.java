package org.nextprot.api.core.service.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.core.domain.EntryUtils;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.annotation.ValidEntry;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Aspect used to validate a given entry. 
 * 
 * @author Daniel Teixeira
 * @version $Revision$, $Date$, $Author$
 */
@Aspect
public class ServiceEntryValidation implements InitializingBean{

	private static final Log LOGGER = LogFactory.getLog(ServiceEntryValidation.class);

	@Autowired
	private MasterIdentifierService masterIdentifierService;
	private Set<String> uniqueNames;

	@Around("execution(* org.nextprot.api.*.service.*.*(..))")
	//@Around("execution(* org.nextprot.api.*.service.*.*(.., @aspects.ValidEntry (*), ..))")
	public Object checkValidEntry(ProceedingJoinPoint pjp) throws Throwable {

		Object[] arguments = pjp.getArgs();
		for (Object arg : arguments) {
			if ((arg != null) && EntryConfig.class.isAssignableFrom(arg.getClass())) {
				
				String argument = ((EntryConfig) arg).getEntryName();
				String entryAccession = EntryUtils.getEntryName(argument);
				
				if (!uniqueNames.contains(entryAccession)) {
					LOGGER.error("neXtProt entry " + argument + " was not found, throwing EntryNotFoundException");
					throw new EntryNotFoundException(argument);
				}
			}

		}

		MethodSignature ms = (MethodSignature) pjp.getSignature();
		Annotation[][] annotations = ms.getMethod().getParameterAnnotations();

		int i = 0;
		for (Annotation[] paramAnnotations : annotations) {
			for (Annotation annotation : paramAnnotations) {
				if (ValidEntry.class.isAssignableFrom(annotation.getClass())) {
					if (!uniqueNames.contains(arguments[i])) {
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

	@Override
	public void afterPropertiesSet() throws Exception {
		//Since there is not modification, this one do not need to be synchronized
		uniqueNames = new HashSet<>(masterIdentifierService.findUniqueNames());
	}

}