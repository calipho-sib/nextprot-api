package org.nextprot.api.etl.statement.charpipeline;

import java.io.IOException;
import java.io.PipedReader;
import java.io.Reader;

/**
 * This class is a source of data for a pipe of threads.  It connects to
 * a sink, but cannot serve as a sink for any other Pipe.  That is, it must
 * always be at the beginning, or "source" of the pipe.  For this class,
 * the source of data is the specified Reader object (such as a FileReader).
 **/
public class ReaderPipeSource extends Pipe {
	protected Reader in;  // The Reader we take data from

	/**
	 * To create a ReaderPipeSource, specify the Reader that data comes from
	 * and the Pipe sink that it should be sent to.
	 **/
	public ReaderPipeSource(Pipe sink, Reader in)
			throws IOException {
		super(sink);
		this.in = in;
	}

	/**
	 * This is the thread body.  When the pipe is started, this method copies
	 * characters from the Reader into the pipe
	 **/
	public void run() {
		try {
			char[] buffer = new char[1024];
			int chars_read;
			while((chars_read = in.read(buffer)) != -1)
				out.write(buffer, 0, chars_read);
		}
		catch (IOException e) {}
		// When done with the data, close the Reader and the pipe
		finally { try { in.close(); out.close(); } catch (IOException e) {} }
	}

	/**
	 * This method overrides the getReader() method of Pipe.  Because this
	 * is a source thread, this method should never be called.  To make sure
	 * that it is never called, we throw an Error if it is.
	 **/
	protected PipedReader getReader() {
		throw new Error("Can't connect to a ReaderPipeSource!");
	}
}
