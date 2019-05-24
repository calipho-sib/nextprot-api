package org.nextprot.api.etl.statement.pipeline;

import java.io.IOException;

/**
 * A Pipe is a kind of thread that is connected to another (possibly null)
 * thread, known as its "sink".  If it has a sink, it creates a PipedStatementWriter
 * stream through which it can write statements to that sink.  It connects
 * its PipedStatementWriter stream to a corresponding PipedStatementReader stream in the sink.
 * It asks the sink to create and return such a PipedStatementReader stream by calling
 * the getReader() method of the sink.
 *
 * In once sense, a Pipe is just a linked list of threads, and the Pipe
 * class defines operations that operate on the whole chain of threads,
 * rather than a single thread.
 **/
public abstract class Pipe implements Runnable {

	private boolean hasStarted;
	protected Pipe sink = null;
	protected PipedStatementWriter out = null;
	protected PipedStatementReader in;
	private Thread thread;

	/**
	 * Create a Pipe and connect it to the specified Pipe
	 **/
	public Pipe(Pipe sink, PipedStatementReader pipedReader) throws IOException {
		this.sink = sink;
		this.in = pipedReader;
		out = new PipedStatementWriter();
		out.connect(sink.getReader());
	}

	public abstract String getName();

	/**
	 * This constructor is for creating terminal Pipe threads--i.e. those
	 * sinks that are at the end of the pipe, and are not connected to any
	 * other threads.
	 **/
	public Pipe(PipedStatementReader pipedReader) {
		super();
		this.in = pipedReader;
	}

	/**
	 * This protected method requests a Pipe threads to create and return
	 * a PipedReader thread so that another Pipe thread can connect to it.
	 **/
	protected PipedStatementReader getReader() {
		return in;
	}

	/**
	 * This and the following methods provide versions of basic Thread methods
	 * that operate on the entire pipe of threads.
	 * This one calls start() on all threads in sink-to-source order.
	 **/
	public void startPipe() {
		if (sink != null) {
			sink.startPipe();
		}
		if (!hasStarted) {
			hasStarted = true;
			thread = new Thread(this, getName());
			thread.start();
			System.out.println("Open pipe "+getName());
		}
	}

	/** Wait for all threads in the pipe to terminate */
	public void joinPipe() throws InterruptedException {
		if (sink != null) sink.joinPipe();
		thread.join();
		System.out.println("Join pipe "+getName());
	}
}
