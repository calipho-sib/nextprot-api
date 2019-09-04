package org.nextprot.api.web.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.web.domain.PepXResponse.PepXEntryMatch;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PepxUtils {

	/**
	 * Read JSON answer from PepX and get a corresponding Java object
	 * @param content
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static PepXResponse parsePepxResponse(String jsonIsString){

	    try {
			ObjectMapper mapper = new ObjectMapper();
	    	return mapper.readValue(jsonIsString, PepXResponse.class);
	    } catch (IOException e) {
			throw new NextProtException("Some error while reading PepX response " + e.getLocalizedMessage());
		}
	}

	public static  PepXResponse getPepXResponse(String pepxBaseUrl, String peptides, boolean modeIsoleucine) {
		
		String httpRequest = pepxBaseUrl + "?format=json" + (modeIsoleucine ? ("&mode=IL&pep=" + peptides) : ("&pep=" + peptides));

		try {

			URL pepXUrl = new URL(httpRequest);
			URLConnection px = pepXUrl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(px.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			
			return PepxUtils.parsePepxResponse(sb.toString());
				
		
		} catch (IOException e) {
			throw new NextProtException(e);
		}
	}

	public static  PepXResponse getPepxResponseByPost(String pepxBaseUrl, String peptides, boolean modeIsoleucine) {
		try {

			URL pepXUrl = new URL(pepxBaseUrl);
			Map<String,Object> params = new LinkedHashMap<>();
			params.put("format", "json");
			params.put("mode", "IL");
			params.put("pep", peptides);

			// POST payload same as the query sting for GET
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String,Object> param : params.entrySet()) {
				if (postData.length() != 0) postData.append('&');
				postData.append(param.getKey());
				postData.append('=');
				postData.append(param.getValue());
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection pepxConnection = (HttpURLConnection) pepXUrl.openConnection();
			pepxConnection.setRequestMethod("POST");
			pepxConnection.setRequestProperty("Content-Type", "text/plain");
			pepxConnection.setFixedLengthStreamingMode(postDataBytes.length);
			pepxConnection.setDoOutput(true);
			pepxConnection.getOutputStream().write(postDataBytes);
//			LOGGER.info("POST payload " + postDataBytes.toString());

			BufferedReader in = new BufferedReader(new InputStreamReader(pepxConnection.getInputStream(), "UTF-8"));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();

			return PepxUtils.parsePepxResponse(sb.toString());

		} catch (IOException e) {
			throw new NextProtException(e);
		}
	}

	
	public static PepXResponse filterOutVariantMatch(PepXResponse in) {
		
		PepXResponse out = new PepXResponse();
		out.setParams(in.getParams());
		out.setPeptideMatches(new ArrayList<>());
		
		// for each peptide
		for (PepXResponse.PepXMatch pmIn : in.getPeptideMatches()) {
			PepXResponse.PepXMatch pmOut = new PepXResponse.PepXMatch();
			pmOut.setPeptide(pmIn.getPeptide());
			pmOut.setEntryMatches(new ArrayList<>());
			out.getPeptideMatches().add(pmOut);

			// for each peptide entry match
			for (PepXEntryMatch emIn :  pmIn.getEntryMatches()) {
				
				// if we have at least an isoform match without variant
				if (emIn.getIsoforms().stream().anyMatch(i -> i.getPosition()==null)) {
					// we create an entry match with the isoform(s) matching with no variant (pos = 0)
					PepXEntryMatch emOut = new PepXEntryMatch();
					emOut.setEntryName(emIn.getEntryName());
					emOut.setIsoforms(
							emIn.getIsoforms().stream().
							filter(i -> i.getPosition()==null).collect(Collectors.toList())
							);
					pmOut.getEntryMatches().add(emOut);
				}
			}
		}
		return out;
	}
	
	
	
}
