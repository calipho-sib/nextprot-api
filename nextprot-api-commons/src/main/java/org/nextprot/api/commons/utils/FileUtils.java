package org.nextprot.api.commons.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class to work with files
 * 
 * @author dteixeira
 */
public class FileUtils {

	public static void writeStringToFile(String fileName, String content) {

		FileWriter fileWriter = null;

		try {
			File newTextFile = new File(fileName);
			fileWriter = new FileWriter(newTextFile);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);

		} finally {
			try {
				fileWriter.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static String readResourceAsString(String resourcePath) {
		InputStream in = FileUtils.class.getResourceAsStream(resourcePath);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
		return readBuffer(buffer);
	}

	private static String readBuffer(BufferedReader reader) {

		StringBuffer data = new StringBuffer();
		try {
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				data.append(readData);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return data.toString();

	}

}
