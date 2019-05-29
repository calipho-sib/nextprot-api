package org.nextprot.api.etl.statement.pipeline;

import java.io.IOException;
import java.util.List;

/**
 * A Pipe is a kind of thread that is connectable to another thread,
 * known as its "receiver". If so, it creates a PipedOutputPort stream
 * through which it can write statements to that receiver.
 *
 * It connects its PipedOutputPort stream to a corresponding PipedInputPort
 * stream in the receiver.
 **/
public abstract class Pipe implements Runnable {

	private boolean hasStarted;

	private final int crossSection;
	protected PipedOutputPort out = null;
	protected PipedInputPort in;

	// the 2 followings should go to pipeline, the creation of thread also
	private Pipe receiver = null;
	private Thread thread;

	/**
	 * Create a Pipe and connect it to the specified Pipe
	 **/
	public Pipe(int crossSection) {
		this.crossSection = crossSection;
		this.in = new PipedInputPort(crossSection);
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

	public int getCrossSection() {

		return crossSection;
	}

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
