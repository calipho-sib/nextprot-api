package org.nextprot.api.web;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.GoogleAnalyticsRequest;
import com.brsanthu.googleanalytics.PageViewHit;
import com.google.common.base.Optional;

public class GoogleAnalyticsFilter extends OncePerRequestFilter {

	private GoogleAnalytics ga = null;
	private final Log Logger = LogFactory.getLog(GoogleAnalyticsFilter.class);

	private String gaTrackingID = null;
	private static final String GA_TRACKING_ID_SYS_PROP = "GATrackingId";
	
	@Override
	public void initFilterBean() {
		if(System.getProperty(GA_TRACKING_ID_SYS_PROP) != null){
			gaTrackingID = System.getProperty(GA_TRACKING_ID_SYS_PROP);
		}
		if(gaTrackingID != null){
			ga = new GoogleAnalytics(gaTrackingID);
			Logger.info("Google Analytics filter initialized with " + gaTrackingID);
		}else {
			Logger.info("Google Analytics not initialized because -D" + GA_TRACKING_ID_SYS_PROP + "=UA-17852148-3 system property was not found.");
		}
	}

	private GoogleAnalyticsRequest<?> generateHit(HttpServletRequest request) {

		PageViewHit hit = new PageViewHit(request.getRequestURL().toString(), request.getPathInfo());

		String clientId = getClientId(request).toString();
		//Logger.debug("Client Id" + clientId);
		Logger.debug("Sending hit: " + request.getRequestURL().toString());

		hit.clientId(getClientId(request).toString());

		// Overriding IP if present from the client
		Optional<String> ip = getClientIP(request);
		if (ip.isPresent()) {
			hit.userIp(ip.get());
		}

		return hit;

	}

	private void sendToGoogleAnalytics(HttpServletRequest request) {
		try {
			ga.postAsync(generateHit(request));
		} catch (Exception e) {
			Logger.error("Failed to send to GA" + e.getMessage());
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws ServletException, IOException {

		if ((ga != null)  && (!"OPTIONS".equals(req.getMethod()))) {
			sendToGoogleAnalytics(req);
		}

		chain.doFilter(req, resp);
	}

	public UUID getClientId(HttpServletRequest request) {

		// Keeping just IP + agent method, because some methods may require
		// authentication and others not. And therefore different UUID would be
		// generated.
		UUID id = getClientUniqueIdentifier(request);
		// Logger.debug("Found UUID " + id + " based on custom headers");
		return id;

	}

	/**
	 * Get a client unique identifier created using the headers
	 * 
	 * @param request
	 * @return
	 */
	public UUID getClientUniqueIdentifier(HttpServletRequest request) {

		StringBuilder sb = new StringBuilder();

		sb.append(request.getHeader("origin") + "; ");
		sb.append(request.getHeader("user-agent") + "; ");
		sb.append(request.getHeader("hostname") + "; ");
		sb.append(request.getHeader("x-forwarded-for") + "; ");
		sb.append(request.getRemoteHost() + "; ");
		sb.append(request.getRemoteUser() + "; ");
		sb.append(request.getRemoteAddr() + "; ");

		Logger.debug("Building UI based on string " + sb.toString());

		return UUID.nameUUIDFromBytes(sb.toString().getBytes());

	}

	/**
	 * Gets the IP of the real client
	 * 
	 * @param request
	 * @return
	 */
	public Optional<String> getClientIP(HttpServletRequest request) {

		String ip = request.getHeader("x-forwarded-for"); // May differ from
															// implementations
		if (ip != null) {
			return Optional.of(ip);
		} else {
			return Optional.absent();
		}

	}

}
