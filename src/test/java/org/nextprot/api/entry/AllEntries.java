package org.nextprot.api.entry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.dbunit.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class used to test all entries
 * 
 * @author dteixeira
 */
@Ignore
public class AllEntries extends MVCBaseIntegrationTest{

	/**
	 * @author mpereira, dteixeira
	 */
	static class RESTCall implements Runnable {

		private String name;

		RESTCall(String name) {
			this.name = name;
		}

		@Override
		public void run() {

			try {

				long start = System.currentTimeMillis();
				URL url = new URL("http://localhost:8080/nextprot-api/entry/" + name + "/genomic-mappings.xml");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				InputStream in = conn.getInputStream();
				
				if(!getStringFromInputStream(in).contains("genomicMappings")){
					System.err.println("Error with " + name);
				}
				// builder.parse(in);
				in.close();
				//System.out.println(name + " finished  in " + (System.currentTimeMillis() - start) + " ms");

			} catch (Exception e) {
				e.printStackTrace();

			}
		}

	}

	
	private static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
 
	@Autowired MasterIdentifierService service;

	@Test
	public void testInParallel(){

		long start = System.currentTimeMillis();

		final int NUMBER_THREADS = 50;

		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

		for (String ac : service.findUniqueNames()) {
			//System.out.println(ac);
			Runnable restCall = new RESTCall(ac);
			executor.execute(restCall);
		}

		// Wait for all threads to stop
		executor.shutdown();
		try {
			executor.awaitTermination(6, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("Finished  in " + (System.currentTimeMillis() - start) + " ms");

	}
	/*
	 * @Test
	 * public void testAllEntriesSequentially() {
	 * List<String> uniqueNamesErrors = new ArrayList<String>();
	 * int errors = 0;
	 * long maxTime = Long.MIN_VALUE;
	 * long minTime = Long.MAX_VALUE;
	 * String maxEntry = "";
	 * String minEntry = "";
	 * List<String> azs = masterIdentifier.findUniqueNames();
	 * long start = System.currentTimeMillis();
	 * int i = 0;
	 * for (String ac : azs) {
	 * try {
	 * long now = System.currentTimeMillis();
	 * URL url = new URL("http://localhost:8080/nextprot-api/entry/" + ac + ".xml");
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET");
	 * InputStream in = conn.getInputStream();
	 * // builder.parse(in);
	 * in.close();
	 * long time = System.currentTimeMillis() - now;
	 * System.out.println(i++ + " - " + ac + ": " + time);
	 * if (time > maxTime) {
	 * maxTime = time;
	 * maxEntry = ac;
	 * } else if (time < minTime) {
	 * minTime = time;
	 * minEntry = ac;
	 * }
	 * } catch (Exception e) {
	 * errors++;
	 * System.err.println("Failed for " + ac);
	 * uniqueNamesErrors.add(ac);
	 * e.printStackTrace();
	 * }
	 * }
	 * System.out.println("Finished  in " + (System.currentTimeMillis() - start) + " ms, max entry was " + maxEntry + " with " + maxTime + " ms and min entry was " + minEntry + " with " + minTime
	 * + " ms and got " + errors + " errors");
	 * System.out.println("Errors for: ");
	 * for (String u : uniqueNamesErrors) {
	 * System.out.println(u);
	 * }
	 * }
	 * @Test
	 * public void testSubListOfEntries() {
	 * List<String> uniqueNamesErrors = new ArrayList<String>();
	 * int errors = 0;
	 * long maxTime = Long.MIN_VALUE;
	 * long minTime = Long.MAX_VALUE;
	 * String maxEntry = "";
	 * String minEntry = "";
	 * List<String> azs = masterIdentifier.findUniqueNames();
	 * long start = System.currentTimeMillis();
	 * System.out.println("Got " + azs.subList(0, 10).size() + " entries");
	 * for (String ac : azs.subList(0, 10)) {
	 * try {
	 * long now = System.currentTimeMillis();
	 * URL url = new URL("http://localhost:8080/nextprot-api/entry/" + ac + ".xml");
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET");
	 * InputStream in = conn.getInputStream();
	 * // builder.parse(in);
	 * in.close();
	 * long time = System.currentTimeMillis() - now;
	 * System.out.println(ac + ": " + time);
	 * if (time > maxTime) {
	 * maxTime = time;
	 * maxEntry = ac;
	 * } else if (time < minTime) {
	 * minTime = time;
	 * minEntry = ac;
	 * }
	 * } catch (Exception e) {
	 * errors++;
	 * System.err.println("Failed for " + ac);
	 * uniqueNamesErrors.add(ac);
	 * e.printStackTrace();
	 * }
	 * }
	 * System.out.println("Finished  in " + (System.currentTimeMillis() - start) + " ms, max entry was " + maxEntry + " with " + maxTime + " ms and min entry was " + minEntry + " with " + minTime
	 * + " ms and got " + errors + " errors");
	 * System.out.println("Errors for: ");
	 * for (String u : uniqueNamesErrors) {
	 * System.out.println(u);
	 * }
	 * }
	 */

}