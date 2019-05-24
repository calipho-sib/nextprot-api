package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.List;

public class PipedStatementWriter {

	private PipedStatementReader sink;

	private boolean closed = false;

	public PipedStatementWriter(PipedStatementReader snk) throws IOException {

		connect(snk);
	}

	public PipedStatementWriter() { }

	public synchronized void connect(PipedStatementReader snk) throws IOException {

		if (snk == null) {
			throw new NullPointerException();
		} else if (sink != null || snk.isConnected()) {
			throw new IOException("Already connected");
		} else if (snk.isClosedByReader() || closed) {
			throw new IOException("Pipe closed");
		}

		sink = snk;
		snk.setIn(-1);
		snk.setOut(0);
		snk.setConnected(true);
	}

	/**
	 * Writes the specified <code>statement</code> to the piped output stream.
	 * If a thread was reading data statements from the connected piped input
	 * stream, but the thread is no longer alive, then an
	 * <code>IOException</code> is thrown.
	 * <p>
	 * Implements the <code>write</code> method of <code>Writer</code>.
	 *
	 * @param      statement  the <code>statement</code> to be written.
	 * @exception  IOException  if the pipe is
	 *          <a href=PipedOutputStream.html#BROKEN> <code>broken</code></a>,
	 *          {@link #connect(PipedStatementReader) unconnected}, closed
	 *          or an I/O error occurs.
	 */
	public void write(Statement statement)  throws IOException {
		if (sink == null) {
			throw new IOException("Pipe not connected");
		}
		sink.receive(statement);
	}

	/**
	 * Writes <code>len</code> statements from the specified statement array
	 * starting at offset <code>off</code> to this piped output stream.
	 * This method blocks until all the characters are written to the output
	 * stream.
	 * If a thread was reading data statements from the connected piped input
	 * stream, but the thread is no longer alive, then an
	 * <code>IOException</code> is thrown.
	 *
	 * @param      sbuf  the data.
	 * @param      off   the start offset in the data.
	 * @param      len   the number of statements to write.
	 * @exception  IOException  if the pipe is
	 *          <a href=PipedOutputStream.html#BROKEN> <code>broken</code></a>,
	 *          {@link #connect(PipedStatementReader) unconnected}, closed
	 *          or an I/O error occurs.
	 */
	public void write(Statement[] sbuf, int off, int len) throws IOException {
		if (sink == null) {
			throw new IOException("Pipe not connected");
		} else if ((off | len | (off + len) | (sbuf.length - (off + len))) < 0) {
			throw new IndexOutOfBoundsException();
		}
		sink.receive(sbuf, off, len);
	}

	public void write(List<Statement> sbuf, int off, int len) throws IOException {
		if (sink == null) {
			throw new IOException("Pipe not connected");
		} else if ((off | len | (off + len) | (sbuf.size() - (off + len))) < 0) {
			throw new IndexOutOfBoundsException();
		}
		sink.receive(sbuf, off, len);
	}

	/**
	 * Flushes this output stream and forces any buffered output statements
	 * to be written out.
	 * This will notify any readers that statements are waiting in the pipe.
	 *
	 * @exception  IOException  if the pipe is closed, or an I/O error occurs.
	 */
	public synchronized void flush() throws IOException {
		if (sink != null) {
			if (sink.isClosedByReader() || closed) {
				throw new IOException("Pipe closed");
			}
			synchronized (sink) {
				sink.notifyAll();
			}
		}
	}

	/**
	 * Closes this piped output stream and releases any system resources
	 * associated with this stream. This stream may no longer be used for
	 * writing statements.
	 *
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void close()  throws IOException {
		closed = true;
		if (sink != null) {
			sink.receivedLast();
		}
	}
}
