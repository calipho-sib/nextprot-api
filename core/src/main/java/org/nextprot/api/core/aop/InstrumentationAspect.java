package org.nextprot.api.core.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.nextprot.api.commons.utils.KeyValueRepresentation;
import org.nextprot.api.commons.utils.StringUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
			request.putAll(extractSecurityInfo());
			request.putAll(extractHttpInfo());


			clientRequestManager.startMonitoringClientRequest(request);

			StringBuilder sb = new StringBuilder();
			sb.append("type=Controller;");
			addMethodParameters(sb, methodSignature);
			addArgumentsParameters(sb, arguments, annotations);
			sb.append("controllerRequestId=");
			sb.append(controllerRequestIdCounter.get());
			sb.append(";");

			for (String key : request.keySet()) {
				sb.append(key);
				sb.append("=");
				sb.append(request.get(key));
				sb.append(";"); 
			}

			long start = System.currentTimeMillis();
			// Proceed to method invocation
			try {

				//LOGGER.info("aspect=before;" + sb);
				Object result = pjp.proceed();
				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addResultParameters(sb, result);
				LOGGER.info("aspect=after;" + sb);

				clientRequestManager.stopMonitoringCurrentRequestInfo();
				controllerRequestId.remove();
				return result;

			} catch (Exception e) {

				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addExceptionParameters(sb, e);
				LOGGER.info("aspect=after;" + sb);

				clientRequestManager.stopMonitoringCurrentRequestInfo(e);
				controllerRequestId.remove();
				throw e;

			}

		} else {
			return pjp.proceed();
		}

	}
	

	@Around("execution(* org.nextprot.api.*.service.*.*(..))")
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
			sb.append("serviceRequestId=");
			sb.append(serviceRequestIdCounter.get());
			sb.append(";");
			Long cId = serviceRequestId.get();
			if (cId != null) {
				sb.append("controllerRequestId=");
				sb.append(cId);
				sb.append(";");
			}

			long start = System.currentTimeMillis();
			try {

				Object result = pjp.proceed();
				//LOGGER.info("aspect=before;" + sb);
				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addResultParameters(sb, result);
				serviceRequestId.remove();
				LOGGER.info("aspect=after;" + sb);
				return result;

			} catch (Exception e) {

				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addExceptionParameters(sb, e);
				serviceRequestId.remove();
				LOGGER.info("aspect=after;" + sb);
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
				sb.append("serviceRequestId=");
				sb.append(sId);
				sb.append(";");
			}

			Long cId = controllerRequestId.get();
			if (cId != null) {
				sb.append("controllerRequestId=");
				sb.append(cId);
				sb.append(";");
			}

			long start = System.currentTimeMillis();
			try {

				//LOGGER.info("aspect=before;" + sb);
				Object result = pjp.proceed();
				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addResultParameters(sb, result);
				LOGGER.info("aspect=after;" + sb);
				return result;

			} catch (Exception e) {

				addTimeElapsed(sb, System.currentTimeMillis() - start);
				addExceptionParameters(sb, e);
				LOGGER.info("aspect=after;" + sb);
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
		sb.append(e.getClass().getName());
		sb.append(";");
		if (e.getMessage() != null){
			sb.append("exceptionMsg=");
			sb.append(e.getMessage().trim().replaceAll("=", "").replaceAll(";", "="));
			sb.append(";");
		}
		return sb;
	}

	
	
	private static Map<String, String> extractHttpInfo(){
		
		HashMap<String, String> map = new HashMap<String, String>();

		HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		map.put("http-request-x-forwarded-for", httpRequest.getHeader("x-forwarded-for"));
		map.put("http-request-x-forwarded-host", httpRequest.getHeader("x-forwarded-host"));
		map.put("http-request-x-forwarded-server", httpRequest.getHeader("x-forwarded-server"));

		map.put("http-request-origin", httpRequest.getHeader("origin"));
		map.put("http-request-accept", StringUtils.quote(httpRequest.getHeader("accept")));
		map.put("http-request-content-type", StringUtils.quote(httpRequest.getHeader("content-type")));

		map.put("http-request-referer", httpRequest.getHeader("referer"));
		//map.put("http-request-user-agent",  StringUtils.quote(httpRequest.getHeader("user-agent")));
		//map.put("http-request-content-type", httpRequest.getContentType());
		
		map.put("http-request-method",  StringUtils.quote(httpRequest.getMethod()));
		map.put("http-request-context-path",  StringUtils.quote(httpRequest.getContextPath()));
		map.put("http-request-query-string",  StringUtils.quote(httpRequest.getQueryString()));
		map.put("http-request-uri",  StringUtils.quote(httpRequest.getRequestURI()));
		map.put("http-request-url",  StringUtils.quote(httpRequest.getRequestURL().toString()));
		
		map.put("http-request-remote-host",  StringUtils.quote(httpRequest.getRemoteHost()));
		map.put("http-request-remote-user",  StringUtils.quote(httpRequest.getRemoteUser()));		
		map.put("http-request-local-name",  StringUtils.quote(httpRequest.getLocalName()));		
		map.put("http-request-local-address",  StringUtils.quote(httpRequest.getLocalAddr()));		
		map.put("http-request-path-info",  StringUtils.quote(httpRequest.getPathInfo()));		
		
		Enumeration<String> params = httpRequest.getParameterNames();
		while (params.hasMoreElements()) {
			String paramName = params.nextElement();
			String paramValue = httpRequest.getParameter(paramName);
			map.put("http-request-param-" + paramName, StringUtils.quote(paramValue));
		}

		return map;
	

	}

	private static Map<String, String> extractSecurityInfo() {

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		HashMap<String, String> map = new HashMap<>();

		if (a != null) {

			if (a.getDetails() instanceof Map) {
				Map<?,?> userDetails = (Map<?,?>) a.getDetails();
				for(Object o : userDetails.keySet()){
					map.put("auth0" + StringUtils.uppercaseFirstLetter(o.toString()), StringUtils.quote(userDetails.get(o).toString()));
				}

			}
			
			if (a.getPrincipal() instanceof UserDetails) {

				UserDetails currentUserDetails = (UserDetails) a.getPrincipal();

				map.put("securityUserName", currentUserDetails.getUsername());
				map.put("securityUserRole",  StringUtils.quote(currentUserDetails.getAuthorities().toString()));

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