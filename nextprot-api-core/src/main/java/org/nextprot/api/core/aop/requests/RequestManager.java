package org.nextprot.api.core.aop.requests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

@Component
public class RequestManager {

	private ThreadLocal<RequestInfo> currentRequest = new ThreadLocal<RequestInfo>();
	public List<RequestInfo> requests = new ArrayList<RequestInfo>();

	public Map<String, RequestInfo> lastAddedRequestByControllers = new TreeMap<String, RequestInfo>();
	public Map<String, RequestInfo> lastFinishedRequestByControllers = new TreeMap<String, RequestInfo>();
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
	
	public synchronized List<RequestInfo> getRequests() {
		return requests;
	}

	public synchronized void startMonitoringClientRequest(RequestInfo requestInfo) {

		currentRequest.set(requestInfo);
		requests.add(requestInfo);
		long start = System.currentTimeMillis();
		currentRequest.get().put(RequestInfo.START_TIME, start);
		currentRequest.get().put(RequestInfo.START_TIME + "Formated",  DATE_FORMAT.format(new Date(start)));
		lastAddedRequestByControllers.put(requestInfo.getType(), requestInfo);
		
	}

	public synchronized void stopMonitoringCurrentRequestInfo() {
		requests.remove(currentRequest.get());
		lastFinishedRequestByControllers.put(currentRequest.get().getType(), currentRequest.get());
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - (Long) (currentRequest.get().get(RequestInfo.START_TIME));
		currentRequest.get().put(RequestInfo.END_TIME, endTime);
		currentRequest.get().put(RequestInfo.END_TIME + "Formated", DATE_FORMAT.format(new Date(endTime)));

		currentRequest.get().put(RequestInfo.ELAPSED_TIME, elapsedTime);
		currentRequest.remove();
	}

	public RequestInfo getCurrentRequest() {
		return currentRequest.get();
	}

	public synchronized Map<String, RequestInfo> getLastAddedRequestByController() {
		return lastAddedRequestByControllers;
	}

	public synchronized Map<String, RequestInfo> getLastFinishedRequest() {
		return lastFinishedRequestByControllers;
	}

	public void stopMonitoringCurrentRequestInfo(Exception e) {
		currentRequest.get().put("exceptionClass", e.getClass().getName());
		currentRequest.get().put("exceptionMesssage", e.getLocalizedMessage());
		stopMonitoringCurrentRequestInfo();
	}

}