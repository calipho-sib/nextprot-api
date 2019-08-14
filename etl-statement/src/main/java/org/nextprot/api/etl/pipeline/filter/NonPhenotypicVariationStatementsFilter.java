package org.nextprot.api.etl.pipeline.filter;

import org.nextprot.commons.statements.Statement;
import org.nextprot.pipeline.statement.core.stage.filter.BaseFilter;
import org.nextprot.pipeline.statement.core.stage.handler.BaseFlowLog;
import org.nextprot.pipeline.statement.core.stage.handler.FlowEventHandler;

import java.io.FileNotFoundException;
import java.util.concurrent.BlockingQueue;

import static org.nextprot.pipeline.statement.core.stage.Source.POISONED_STATEMENT;

public class NonPhenotypicVariationStatementsFilter extends BaseFilter {

	public NonPhenotypicVariationStatementsFilter(int capacity) {

		super(capacity);
	}

	@Override
	public NonPhenotypicVariationStatementsFilter duplicate(int newCapacity) {

		return new NonPhenotypicVariationStatementsFilter(newCapacity);
	}

	@Override
	protected FlowEventHandler createFlowEventHandler() throws Exception {

		return new FlowLog(Thread.currentThread().getName());
	}

	@Override
	public boolean filter(BlockingQueue<Statement> in, BlockingQueue<Statement> out) throws Exception {

		Statement current = in.take();

		if (!"phenotypic-variation".equals(current.getAnnotationCategory())) {
			((FlowLog)getFlowEventHandler()).statementHandled(current, in, out);

			out.put(current);
		}

		return current == POISONED_STATEMENT;
	}

	private static class FlowLog extends BaseFlowLog {

		private FlowLog(String threadName) throws FileNotFoundException {

			super(threadName);
		}

		public void beginOfFlow() {

			sendMessage("opened");
		}

		private void statementHandled(Statement statement, BlockingQueue<Statement> sinkChannel,
		                              BlockingQueue<Statement> sourceChannel) {

			statementHandled("transmit", statement, sinkChannel, sourceChannel);
		}

		@Override
		public void endOfFlow() {

			sendMessage(getStatementCount()+" healthy statements passed to next filter");
		}
	}
}