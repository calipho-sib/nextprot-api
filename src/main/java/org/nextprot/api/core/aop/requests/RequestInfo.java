package org.nextprot.api.core.aop.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class RequestInfo extends TreeMap<String, Object>{

	public static final String START_TIME = "timeStart";
	public static final String END_TIME = "timeEnd";
	public static final String ELAPSED_TIME = "timeElapsed";
	public static final String CONTROLLER_CLASS_NAME = "controllerClassName";

	private static final long serialVersionUID = 6240321400072016657L;
	private static AtomicLong id = new AtomicLong();

	private List<Map<String, Object>> services = new ArrayList<Map<String, Object>>();

	public void setServices(List<Map<String, Object>> services) {
		this.services = services;
	}

	public List<Map<String, Object>> getServices() {
		return services;
	}

	public RequestInfo(String type) {
		super();
		this.type = type;
		this.put("id", String.valueOf(id.incrementAndGet()));
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private String type;

	public String getRemoteAddr() {
		return (get("remoteAddr") != null) ? this.get("remoteAddr").toString() : null;
	}
	
	


}