package org.nextprot.api.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RestErrorResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	private String message;
	private String type;
	//TODO include maven version
	private String about = "neXtProt API - https://api.nextprot.org";
	private final Map<String, Object> properties = new HashMap<>();

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public Map<String, Object> getProperties() {
        return properties;
    }

	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
}
