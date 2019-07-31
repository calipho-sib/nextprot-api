package org.nextprot.api.etl.pipeline;

import org.nextprot.pipeline.statement.core.Pipeline;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Timer implements Pipeline.Monitorable {

	private Instant start;
	private final Map<String, Long> infos;

	public Timer() {
		this.infos = new HashMap<>();
	}

	@Override
	public void started() {

		start = Instant.now();
	}

	@Override
	public void ended() {

		infos.put("elapsed", Duration.between(start, Instant.now()).toMillis());
	}

	public long getElapsedTimeInMs() {

		return infos.get("elapsed");
	}
}
