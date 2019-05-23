package org.nextprot.api.etl.statement.pipeline;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.BufferableStatementReader;

import java.io.IOException;

public class StatementPumpTest {

	@Test
	public void pump() throws IOException {

		StatementPump pump = new StatementPump(mockBufferableStatementReader());

		Assert.assertNotNull(pump.pump());
		Assert.assertNull(pump.pump());
	}

	@Test
	public void isNotEmpty() throws IOException {

		StatementPump pump = new StatementPump(mockBufferableStatementReader());

		Assert.assertFalse(pump.isEmpty());
	}

	@Test
	public void isEmptyAfterOnePump() throws IOException {

		StatementPump pump = new StatementPump(mockBufferableStatementReader());

		Assert.assertFalse(pump.isEmpty());
		Assert.assertTrue(pump.isEmpty());
	}

	private BufferableStatementReader mockBufferableStatementReader() throws IOException {

		BufferableStatementReader reader = Mockito.mock(BufferableStatementReader.class);

		Statement statement = new Statement();

		Mockito.when(reader.nextStatement())
				.thenReturn(statement)
				.thenReturn(null);

		Mockito.when(reader.hasStatement())
				.thenReturn(false)
				.thenReturn(true);

		return reader;
	}
}