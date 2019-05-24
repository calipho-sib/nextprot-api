package org.nextprot.api.etl.statement.pipeline2;

import org.nextprot.api.etl.statement.charpipeline.Pipe;

import java.io.IOException;

/**
 * This class is a sink for data from a pipe of threads.  It can be connected
 * to by other Pipe, but its constructor is not passed a Pipe sink for it
 * to connect to.  That is, it must always be at the end or "sink" of a
 * pipe.  It writes the characters into a specified Writer (such as a
 * FileWriter).
 **/
public class StatementWriterPipeSink extends Pipe {

	/**
	 * To create a WriterPipeSink, just specify what Writer characters
	 * from the pipe should be written to
	 **/
	public StatementWriterPipeSink() throws IOException {
		super();  // Create a terminal Pipe with no sink attached.
		this.out = out;
	}

	/**
	 * This is the thread body for this sink.  When the pipe is started, it
	 * copies characters from the pipe into the specified Writer.
	 **/
	public void run() {
		try {
			char[] buffer = new char[1024];
			int chars_read;
			while((chars_read = in.read(buffer)) != -1)
				out.write(buffer, 0, chars_read);
		}
		catch (IOException e) {}
		// When done with the data, close the pipe and flush the Writer
		finally { try {in.close(); out.flush(); } catch (IOException e) {} }
	}
}
