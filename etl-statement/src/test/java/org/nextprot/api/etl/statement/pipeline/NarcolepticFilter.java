package org.nextprot.api.etl.statement.pipeline;


import org.nextprot.commons.statements.Statement;

import java.io.IOException;

/**
 * This filter just transmit statements from PipedInputPort to PipedInputPort
 * and take a nap
 */
class NarcolepticFilter extends PipedFilter {

	private static int COUNT = 0;

	private final int takeANapInMillis;
	private final int id;

	public NarcolepticFilter(int crossSection) {

		this(crossSection, -1);
	}

	public NarcolepticFilter(int crossSection, int takeANapInMillis) {

		super(crossSection);
		this.takeANapInMillis = takeANapInMillis;

		id = ++COUNT;
	}

	@Override
	public String getName() {

		return getClass().getSimpleName()+"-"+id;
	}

	@Override
	public boolean filter(PipedInputPort in, PipedOutputPort out) throws IOException {

		Statement[] buffer = new Statement[getCrossSection()];

		int numOfStatements = in.read(buffer, 0, getCrossSection());

		for (int i=0 ; i<numOfStatements ; i++) {

			out.write(buffer[i]);

			if (buffer[i] == END_OF_FLOW_TOKEN) {

				return true;
			} else {

				System.out.println(Thread.currentThread().getName()
						+ ": filter statement "+ buffer[i].getStatementId());
			}
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
