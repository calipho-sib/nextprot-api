package org.nextprot.api.etl.statement.pipeline;


import org.nextprot.commons.statements.Statement;

import java.io.IOException;

/**
 * This filter just transmit statements from PipedInputPort to PipedInputPort
 * and take a nap
 */
public class NarcolepticFilter extends PipedFilter {

	private static int COUNT = 0;

	private final int takeANapInMillis;

	public NarcolepticFilter(int crossSection) {

		this(crossSection, 1000);
	}

	public NarcolepticFilter(int crossSection, int takeANapInMillis) {

		super(crossSection);
		this.takeANapInMillis = takeANapInMillis;

		COUNT++;
	}

	@Override
	public String getName() {

		return getClass().getSimpleName()+"-"+COUNT;
	}

	@Override
	public boolean filter(PipedInputPort in, PipedOutputPort out) throws IOException {

		Statement[] buffer = new Statement[getCrossSection()];

		int numOfStatements = in.read(buffer, 0, getCrossSection());

		for (int i=0 ; i<numOfStatements ; i++) {

			if (buffer[i] == null) {

				return true;
			}

			System.out.println(Thread.currentThread().getName()
					+ ": filter statement "+ buffer[i].getStatementId());

			out.write(buffer[i]);
		}

		if (takeANapInMillis > 0) {
			try {
				System.out.println(Thread.currentThread().getName()
						+ ": filter statement take a nap");
				Thread.sleep(takeANapInMillis);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}

		return false;
	}
}
