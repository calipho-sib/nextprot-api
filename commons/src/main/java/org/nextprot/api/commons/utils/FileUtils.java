package org.nextprot.api.commons.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

}
