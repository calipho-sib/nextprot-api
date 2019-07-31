package org.nextprot.api.etl.pipeline.filter;

import org.nextprot.api.etl.pipeline.NXFlatDB;
import org.nextprot.commons.statements.Statement;
import org.nextprot.pipeline.statement.core.stage.filter.BaseFilter;
import org.nextprot.pipeline.statement.core.stage.handler.BaseFlowLog;
import org.nextprot.pipeline.statement.core.stage.handler.FlowEventHandler;

import java.io.FileNotFoundException;
import java.util.concurrent.BlockingQueue;

import static org.nextprot.pipeline.statement.core.stage.Source.POISONED_STATEMENT;

public class NxFlatRawTableFilter extends BaseFilter {

	private final NXFlatDB.Table table = NXFlatDB.Table.raw_statements;

	public NxFlatRawTableFilter(int capacity) {

		super(capacity);
	}

	@Override
	public org.nextprot.pipeline.statement.nxflat.filter.NxFlatRawTableFilter duplicate(int newCapacity) {

		return new org.nextprot.pipeline.statement.nxflat.filter.NxFlatRawTableFilter(newCapacity);
	}

	@Override
	protected FlowEventHandler createFlowEventHandler() throws Exception {

		return new FlowLog(Thread.currentThread().getName(), table);
	}

	@Override
	public boolean filter(BlockingQueue<Statement> in, BlockingQueue<Statement> out) throws Exception {

		Statement current = in.take();

		((FlowLog)getFlowEventHandler()).statementHandled(current, in, out);

		out.put(current);

		return current == POISONED_STATEMENT;
	}

	private static class FlowLog extends BaseFlowLog {

		private final NXFlatDB.Table table;

		private FlowLog(String threadName, NXFlatDB.Table table) throws FileNotFoundException {

			super(threadName);
			this.table = table;
		}

		public void beginOfFlow() {

			sendMessage("opened");
		}

		private void statementHandled(Statement statement, BlockingQueue<Statement> sinkChannel,
		                              BlockingQueue<Statement> sourceChannel) {

			statementHandled("load and transmit", statement, sinkChannel, sourceChannel);
		}

		@Override
		public void endOfFlow() {

			sendMessage(getStatementCount()+" healthy statements loaded in table "+ table + " and passed to next filter");
		}
	}
}