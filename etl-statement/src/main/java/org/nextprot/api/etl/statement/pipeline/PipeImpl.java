package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class PipeImpl implements Pipe {

	private Queue<Statement> buffer = new LinkedList<>();
	private boolean isOpenForWriting = true;
	private boolean hasReadLastObject = false;

	@Override
	public synchronized boolean spill(Statement statement) {

		if (!isOpenForWriting) {
			throw new RuntimeException(new IOException("pipe is closed; cannot write statement to it"));
		} else if (statement == null) {
			throw new IllegalArgumentException("cannot push null statement in pipe; null is reserved for pipe-empty sentinel value");
		}

		boolean wasAdded = buffer.add(statement);
		notify();

		return wasAdded;
	}

	@Override
	// not using next() and willHaveNext() because a currently-empty pipe might be
	//  closed after the willHaveNext() check, causing next() to wait forever
	// not using an exception because would require consumers to write unidiomatic `while(true)`
	// not using an Option because there is no standard Option and reimplementing it is too annoying
	public synchronized Statement spillNextOrNullIfEmptied() throws InterruptedException {

		if (hasReadLastObject) {
			throw new NoSuchElementException("pipe is closed and empty; will never contain any further values");
		}

		while (buffer.isEmpty()) {
			wait(); // pipe empty - wait
		}

		Statement statement = buffer.remove();
		if (statement == null) { // will be null if it's the last element
			hasReadLastObject = true;
		}
		return statement;
	}

	@Override
	public synchronized void closeForWriting() {
		isOpenForWriting = false;
		buffer.add(null);
		notify();
	}
}
