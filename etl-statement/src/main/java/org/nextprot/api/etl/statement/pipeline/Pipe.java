package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

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

	static final Statement END_OF_FLOW_TOKEN = null;

	private boolean hasStarted;

	private final int sectionWidth;
	protected PipedOutputPort out = null;
	protected PipedInputPort in;

	// the 2 followings should go to pipeline, the creation of thread also
	private Pipe receiver = null;

	/**
	 * Create a Pipe and connect it to the specified Pipe
	 **/
	public Pipe(int sectionWidth) {
		this.sectionWidth = sectionWidth;
		this.in = new PipedInputPort(sectionWidth);
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

	public int getSectionWidth() {

		return sectionWidth;
	}

	/**
	 * This protected method requests a Pipe threads to create and return
	 * a PipedInputPort thread so that another Pipe thread can connect to it.
	 **/
	protected PipedInputPort getInputPort() {
		return in;
	}

	public void openPipe(List<Thread> collector) {

		if (!hasStarted) {
			hasStarted = true;
			Thread thread = new Thread(this, getName());
			thread.start();
			collector.add(thread);
			System.out.println("Pipe "+getName()+": opened (section width="+sectionWidth+")");
		}

		if (receiver != null) {
			receiver.openPipe(collector);
		}
	}

	@Override
	public void run() {

		try {
			handleFlow();
			endOfFlow();
		}
		catch (IOException e) {
			System.err.println(e.getMessage() + " in thread " + Thread.currentThread().getName());
		}
		// When done with the data, close the pipe and flush the Writer
		finally {
			try {
				closePipe();
			} catch (IOException e) {
				System.err.println(Thread.currentThread().getName() + ": could not close the pipe, e="+e.getMessage());
			}
		}
	}

	protected void endOfFlow() {

		System.out.println(Thread.currentThread().getName() + ": end of flow");
	}

	protected void closePipe() throws IOException {

		try {
			if (in != null) {
				in.close();
				System.out.println(Thread.currentThread().getName() + ": input port closed");
			}
			if (out != null) {
				out.close();
				System.out.println(Thread.currentThread().getName() + ": output port closed");
			}
		} catch (IOException e) {
			System.err.println(e.getMessage() + " in thread " + Thread.currentThread().getName());
		}
	}

	protected abstract void handleFlow() throws IOException;
	protected abstract String getName();
}
