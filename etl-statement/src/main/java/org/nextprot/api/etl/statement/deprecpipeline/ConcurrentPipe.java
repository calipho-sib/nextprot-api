package org.nextprot.api.etl.statement.deprecpipeline;


/**
 * -----------------------------------
 *  IN        ------------>       OUT
 * -----------------------------------
 */
public abstract class ConcurrentPipe implements Runnable {

	private boolean isStarted = false;

	public void startPipe() {

		if (!isStarted) {

			isStarted = true;
			Thread thread = new Thread(this);
			thread.start();
		}
	}

	public void stopPipe() {

		isStarted = false;
	}

	/**
	 * make your thread sleep so you can confirm that other threads still run in the meantime
	 * for debugging purposes only
	 */
	protected void delayForDebug(long millis) {

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
