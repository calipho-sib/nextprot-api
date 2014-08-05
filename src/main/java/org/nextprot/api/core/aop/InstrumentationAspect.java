package org.nextprot.api.core.aop;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.nextprot.api.commons.utils.KeyValueRepresentation;
import org.nextprot.api.core.aop.requests.RequestInfo;
import org.nextprot.api.core.aop.requests.RequestInfoFactory;
import org.nextprot.api.core.aop.requests.RequestManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect responsible to instrument the actions of the users
 * 
 * @author Daniel Teixeira
 * @version $Revision$, $Date$, $Author$
 */
@Aspect
@ManagedResource(objectName = "org.nextprot.api:name=ControllersInstrumentation", description = "My Managed Bean", log = true, logFile = "jmx.log", persistPeriod = 200, persistLocation = "/tmp", persistName = "bar")
public class InstrumentationAspect {

	@Autowired
	private RequestManager clientRequestManager;

	private static final Log LOGGER = LogFactory.getLog(InstrumentationAspect.class);

	private boolean enableInstrumentation = true;

	private AtomicLong daoIdCounter = new AtomicLong();

	private AtomicLong controllerRequestIdCounter = new AtomicLong();
	private ThreadLocal<Long> controllerRequestId = new ThreadLocal<Long>();

	private AtomicLong serviceRequestIdCounter = new AtomicLong();
	private ThreadLocal<Long> serviceRequestId = new ThreadLocal<Long>();

	@Around("execution(* org.nextprot.api.*.controller.*.*(..))")
	public Object logServiceInformaton(ProceedingJoinPoint pjp) throws Throwable {

		if (enableInstrumentation) {

			controllerRequestId.set(controllerRequestIdCounter.incrementAndGet());

			MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
			Annotation[][] annotations = methodSignature.getMethod().getParameterAnnotations();
			Object[] arguments = pjp.getArgs();

			RequestInfo request = RequestInfoFactory.createRequestInfo(methodSignature.getDeclaringType().getSimpleName() + "#" + methodSignature.getName());
			Map<String, String> map = extractSecurityInfo(arguments);

			ServletRequestAttributes atts = (ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes());
			if (atts != null && atts.getRequest() != null) {
				map.put("remoteAddr", atts.getRequest().getRemoteAddr());
				map.put("remoteHost", atts.getRequest().getRemoteHost());
			}

			request.putAll(map);

			clientRequestManager.startMonitoringClientRequest(request);

			StringBuilder sb = new StringBuilder();
			sb.append("type=Controller;");
			addMethodParameters(sb, methodSignature);
			addArgumentsParameters(sb, arguments, annotations);
			sb.append("ControllerId=");
			sb.append(controllerRequestIdCounter.get());
			sb.append(";");

			for (String key : map.keySet()) {
				sb.append(key);
				sb.append("=");
				sb.append(map.get(key));
				sb.append(";");
			}

			long start = System.currentTimeMillis();
			// Proceed to method invocation
			try {

				Object result = pjp.proceed();
				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addResultParameters(sb, result);
				LOGGER.info(sb);

				clientRequestManager.stopMonitoringCurrentRequestInfo();
				controllerRequestId.remove();
				return result;

			} catch (Exception e) {

				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addExceptionParameters(sb, e);
				LOGGER.info(sb);

				clientRequestManager.stopMonitoringCurrentRequestInfo(e);
				controllerRequestId.remove();
				throw e;

			}

		} else {
			return pjp.proceed();
		}

	}

	@Around("execution(* org.nextprot.*.service.*.*(..))")
	public Object logService(ProceedingJoinPoint pjp) throws Throwable {

		if (enableInstrumentation) {

			serviceRequestId.set(serviceRequestIdCounter.incrementAndGet());

			MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
			Annotation[][] annotations = methodSignature.getMethod().getParameterAnnotations();
			Object[] arguments = pjp.getArgs();

			StringBuilder sb = new StringBuilder();
			sb.append("type=Service;");
			addMethodParameters(sb, methodSignature);
			addArgumentsParameters(sb, arguments, annotations);
			sb.append("ServiceId=");
			sb.append(serviceRequestIdCounter.get());
			sb.append(";");
			Long cId = serviceRequestId.get();
			if (cId != null) {
				sb.append("controllerId=");
				sb.append(cId);
				sb.append(";");
			}

			long start = System.currentTimeMillis();
			try {

				Object result = pjp.proceed();
				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addResultParameters(sb, result);
				serviceRequestId.remove();
				LOGGER.info(sb);
				return result;

			} catch (Exception e) {

				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addExceptionParameters(sb, e);
				serviceRequestId.remove();
				LOGGER.info(sb);
				throw e;
			}

		} else {
			return pjp.proceed();
		}

	}

	@Around("execution(* org.nextprot.api.*.dao.*.*(..))")
	public Object logDao(ProceedingJoinPoint pjp) throws Throwable {

		if (enableInstrumentation) {

			MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
			Annotation[][] annotations = methodSignature.getMethod().getParameterAnnotations();
			Object[] arguments = pjp.getArgs();

			StringBuilder sb = new StringBuilder();
			sb.append("type=DAO;");
			addMethodParameters(sb, methodSignature);
			addArgumentsParameters(sb, arguments, annotations);
			sb.append("DAOId=");
			sb.append(daoIdCounter.incrementAndGet());
			sb.append(";");

			Long sId = serviceRequestId.get();
			if (sId != null) {
				sb.append("serviceId=");
				sb.append(sId);
				sb.append(";");
			}

			Long cId = controllerRequestId.get();
			if (cId != null) {
				sb.append("controllerId=");
				sb.append(cId);
				sb.append(";");
			}

			long start = System.currentTimeMillis();
			try {

				Object result = pjp.proceed();
				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addResultParameters(sb, result);
				LOGGER.info(sb);
				return result;

			} catch (Exception e) {

				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addExceptionParameters(sb, e);
				LOGGER.info(sb);
				throw e;
			}

		} else {
			return pjp.proceed();
		}

	}

	private static StringBuilder addArgumentsParameters(StringBuilder sb, Object[] arguments, Annotation[][] annotations) {
		for (int i = 0; i < arguments.length; i++) {
			Annotation[] annots = annotations[i];
			Value v = null;
			for (Annotation a : annots) {
				if (a.annotationType().equals(Value.class)) {
					v = (Value) a;
					break;
				}
			}

			if (v == null) {
				sb.append("arg" + (i + 1));
			} else {
				sb.append(v.value());
			}

			sb.append("=");

			Object argument = arguments[i];
			if (argument == null) {
				sb.append("null");
			} else if (argument instanceof KeyValueRepresentation) {
				sb.append(((KeyValueRepresentation)argument).toKeyValueString());
			} else if (argument instanceof Collection) {
				sb.append(((Collection<?>) argument).size());
			} else if (argument instanceof String || argument instanceof Long || argument instanceof Short || argument instanceof Integer) {
				sb.append(argument);
			} else {
				sb.append("representation-unknown");
			}

			sb.append(";");

		}
		return sb;
	}

	private static StringBuilder addTimeElapsed(StringBuilder sb, long time) {
		sb.append("timeElapsed=");
		sb.append(time);
		sb.append(";");
		return sb;
	}

	private static StringBuilder addMethodParameters(StringBuilder sb, MethodSignature methodSignature) {
		sb.append("class=");
		sb.append(methodSignature.getDeclaringType().getSimpleName());
		sb.append(";");
		sb.append("method=");
		sb.append(methodSignature.getName());
		sb.append(";");
		sb.append("timeStart=");
		sb.append(System.currentTimeMillis());
		sb.append(";");
		return sb;
	}

	private static StringBuilder addResultParameters(StringBuilder sb, Object result) {
		sb.append("timeEnd=");
		sb.append(System.currentTimeMillis());
		sb.append(";");
		if (result instanceof Collection) {
			sb.append("resultSize=");
			sb.append(Collection.class.cast(result).size());
			sb.append(";");
		}else if (result instanceof KeyValueRepresentation) {
			sb.append(KeyValueRepresentation.class.cast(result).toKeyValueString());
		}
		return sb;
	}

	private static StringBuilder addExceptionParameters(StringBuilder sb, Exception e) {
		sb.append("timeEnd=");
		sb.append(System.currentTimeMillis());
		sb.append(";");
		sb.append("exception=");
		sb.append(";");
		if (e.getMessage() != null)
			sb.append(e.getMessage().trim().replaceAll("=", "").replaceAll(";", "="));
		return sb;
	}

	private static Map<String, String> extractSecurityInfo(Object[] arguments) {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		HashMap<String, String> map = new HashMap<String, String>();

		if (a != null) {

			if (a.getPrincipal() instanceof UserDetails) {

				UserDetails currentUserDetails = (UserDetails) a.getPrincipal();

				map.put("securityUserName", currentUserDetails.getUsername());
				map.put("securityUserRole", currentUserDetails.getAuthorities().iterator().next().getAuthority());

			} else {
				map.put("securityUserName", a.getPrincipal().toString());
			}

		} else {
			map.put("securityUserName", "unknown");
		}

		return map;

	}

	@ManagedAttribute
	public boolean getInstrumentationEnabled() {
		return enableInstrumentation;
	}

	@ManagedAttribute
	public void setInstrumentationEnabled(boolean enableInstrumentation) {
		this.enableInstrumentation = enableInstrumentation;
	}

}