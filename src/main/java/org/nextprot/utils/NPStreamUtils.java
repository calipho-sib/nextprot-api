package org.nextprot.utils;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.domain.exception.NextProtException;

/**
 * Helper class to stream data and dealing with exception, like when user cancels a request.
 * @author dteixeira
 *
 */
public class NPStreamUtils {
	
	private static final Log LOGGER = LogFactory.getLog(NPStreamUtils.class);
	private static int BYTES_DOWNLOAD = 10 * 1024 * 1024; // 10 MB

	public static void printOutput(Queue<Future<File>> futuresQueue, HttpServletResponse response) {
		
		boolean userHasCanceled = false;
		OutputStream out = null;
		Future<File> currentFuture = null;
		
		while (!futuresQueue.isEmpty()) {

			currentFuture = futuresQueue.remove();

			InputStream currentInputStream = null;
			try {
		
				if(out == null){
					out = response.getOutputStream();
				}

				File f = currentFuture.get();
				currentInputStream = new FileInputStream(f);
				LOGGER.info("Content of " + f.getName() + " is being streamed to the client");
				int read = 0;
				byte[] bytes = new byte[BYTES_DOWNLOAD];

				while ((read = currentInputStream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				currentInputStream.close();
				out.flush();
			
			} catch (EOFException e) {

				LOGGER.info("User has cancelled the request");
				cancelFutures(currentFuture, futuresQueue);
				userHasCanceled = true;
	
			} catch (Exception e) {
	
				LOGGER.error("Something when wrong when streaming the data" + e.getMessage());
				
				e.printStackTrace();
				cancelFutures(currentFuture, futuresQueue);
				closeStream(out);

				throw new NextProtException(e.getClass().getName() + ": " + e.getMessage());
				
			} finally {
				closeStream(currentInputStream);
			}
			
			
		}

		//No need to close stream when the user has canceled because it is already closed
		if(!userHasCanceled){
			closeStream(out);
		}


	}
	
	private static void closeStream(Closeable stream){

		// Closes
		try {
			if(stream != null){
				stream.close();
			}
		} catch (IOException e) {
				e.printStackTrace();
				throw new NextProtException(e.getClass().getName() + ": " + e.getMessage());
		}

	}
	
	private static void cancelFutures(Future<File> currentFuture, Queue<Future<File>> futuresQueue) {

		if(currentFuture != null)
			currentFuture.cancel(true);

		if(!futuresQueue.isEmpty()){
			System.out.println("Canceling " + futuresQueue.size()  + " tasks");
		}
		
		while (!futuresQueue.isEmpty()) {
			 futuresQueue.remove().cancel(true);
		}

	}
}
