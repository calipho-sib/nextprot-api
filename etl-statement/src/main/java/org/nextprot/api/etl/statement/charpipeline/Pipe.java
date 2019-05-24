package org.nextprot.api.etl.statement.charpipeline;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * A Pipe is a kind of thread that is connected to another (possibly null)
 * thread, known as its "sink".  If it has a sink, it creates a PipedWriter
 * stream through which it can write characters to that sink.  It connects
 * its PipedWriter stream to a corresponding PipedReader stream in the sink.
 * It asks the sink to create and return such a PipedReader stream by calling
 * the getReader() method of the sink.
 *
 * In once sense, a Pipe is just a linked list of threads, and the Pipe
 * class defines operations that operate on the whole chain of threads,
 * rather than a single thread.
 **/
public abstract class Pipe extends Thread {
	protected Pipe sink = null;
	protected PipedWriter out = null;
	protected PipedReader in = null;

	/**
	 * Create a Pipe and connect it to the specified Pipe
	 **/
	public Pipe(Pipe sink) throws IOException {
		this.sink = sink;
		out = new PipedWriter();
		out.connect(sink.getReader());
	}

	/**
	 * This constructor is for creating terminal Pipe threads--i.e. those
	 * sinks that are at the end of the pipe, and are not connected to any
	 * other threads.
	 **/
	public Pipe() { super(); }

	/**
	 * This protected method requests a Pipe threads to create and return
	 * a PipedReader thread so that another Pipe thread can connect to it.
	 **/
	protected PipedReader getReader() {
		if (in == null) in = new PipedReader();
		return in;
	}

	/**
	 * This and the following methods provide versions of basic Thread methods
	 * that operate on the entire pipe of threads.
	 * This one calls start() on all threads in sink-to-source order.
	 **/
	public void startPipe() {
		if (sink != null) sink.startPipe();
		this.start();
	}

	/** Call resume() on all threads in the pipe, in sink-to-source order */
	public void resumePipe() {
		if (sink != null)  sink.resumePipe();
		this.resume();
	}

	/** Call stop() on all threads in the pipe, in source-to-sink order */
	public void stopPipe() {
		this.stop();
		if (sink != null) sink.stopPipe();
	}

	/** Call suspend() on all threads in the pipe, in source-to-sink order */
	public void suspendPipe() {
		this.suspend();
		if (sink != null) sink.suspendPipe();
	}

	/** Wait for all threads in the pipe to terminate */
	public void joinPipe() throws InterruptedException {
		if (sink != null) sink.joinPipe();
		this.join();
	}
}
