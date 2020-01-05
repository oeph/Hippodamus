package dd.kms.hippodamus.testUtils;

public class TestUtils
{
	public static final boolean[]	BOOLEANS	= { false, true };

	private static final long	SLEEP_INTERVAL_MS	= 100;

	public static void sleep(long timeMs) {
		long end = System.currentTimeMillis() + timeMs;
		try {
			do {
				Thread.sleep(SLEEP_INTERVAL_MS);
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
			} while (System.currentTimeMillis() < end);
		} catch (InterruptedException e) {
			try {
				Thread.sleep(SLEEP_INTERVAL_MS);
			} catch (InterruptedException ignored) {

			}
		}
	}
}
