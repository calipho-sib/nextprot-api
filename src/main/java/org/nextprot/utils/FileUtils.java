package org.nextprot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class to work with files
 * 
 * @author dteixeira
 */
public class FileUtils {

	/**
	 * Reads a file as a string and throw a runtime exception if file not found or other IO exception
	 * 
	 * @param filePath
	 * @return the String content of the file
	 */
	public static String readFileAsString(String filePath) {
		try {
			StringBuffer fileData = new StringBuffer();
			BufferedReader reader;

			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
			reader.close();
			return fileData.toString();

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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

}
