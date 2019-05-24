package org.nextprot.api.etl.statement.charpipeline;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * This abstract class simplifies (somewhat) the task of writing a
 * filter pipe--i.e. one that reads data from one Pipe thread, filters
 * it somehow, and writes the results to some other Pipe.
 **/
public abstract class PipeFilter extends Pipe {
	public PipeFilter(Pipe sink) throws IOException { super(sink); }

	public void run() {
		try { filter(in, out); }
		catch (IOException e) {}
		finally { try { in.close(); out.close(); } catch (IOException e) {} }
	}

	/** The method that subclasses must implement to do the filtering */
	abstract public void filter(Reader in, Writer out) throws IOException;
}
