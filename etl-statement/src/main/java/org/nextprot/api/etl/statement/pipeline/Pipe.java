package org.nextprot.api.etl.statement.pipeline;

import java.io.IOException;
import java.util.List;

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

	protected PipedOutputPort out = null;
	protected PipedInputPort in;

	// the 2 followings should go to pipeline, the creation of thread also
	private Pipe receiver = null;
	private Thread thread;

	/**
	 * Create a Pipe and connect it to the specified Pipe
	 **/
	public Pipe(PipedInputPort inputPort) {
		this.in = inputPort;
	}

	/**
	 * Connect this pipe with the receiver pipe
	 * @param receiver
	 * @throws IOException
	 */
	public void connect(Pipe receiver) throws IOException {

		this.receiver = receiver;
		out = new PipedOutputPort();
		out.connect(receiver.getInputPort());
	}

	public abstract String getName();

	/**
	 * This protected method requests a Pipe threads to create and return
	 * a PipedInputPort thread so that another Pipe thread can connect to it.
	 **/
	protected PipedInputPort getInputPort() {
		return in;
	}

	/**
	 * This and the following methods provide versions of basic Thread methods
	 * that operate on the entire pipe of threads.
	 * This one calls start() on all threads in sink-to-source order.
	 **/
	public void openPipe() {

		if (receiver != null) {
			receiver.openPipe();
		}
		if (!hasStarted) {
			hasStarted = true;
			thread = new Thread(this, getName());
			thread.start();
			System.out.println("Pipe "+getName()+": opened");
		}
	}

	/** Wait for all threads in the pipe to terminate */
	public void waitForThePipesToComplete() throws InterruptedException {

		if (receiver != null) {
			receiver.waitForThePipesToComplete();
		}
		thread.join();
		System.out.println("Pipe "+getName()+": closed");
	}

	public void openPipe(List<Thread> threads) {

		if (receiver != null) {
			receiver.openPipe(threads);
		}
		if (!hasStarted) {
			hasStarted = true;
			Thread thread = new Thread(this, getName());
			thread.start();
			threads.add(thread);
			System.out.println("Pipe "+getName()+": opened");
		}
	}
}
