package dd.kms.hippodamus;

import com.google.common.base.Preconditions;
import dd.kms.hippodamus.common.ValueContainer;
import dd.kms.hippodamus.coordinator.ExecutorServiceIds;
import dd.kms.hippodamus.coordinator.TaskCoordinator;
import dd.kms.hippodamus.coordinator.TaskCoordinators;
import dd.kms.hippodamus.handles.Handle;
import dd.kms.hippodamus.testUtils.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static dd.kms.hippodamus.coordinator.ExecutorServiceIds.REGULAR;

/**
 * This test focuses on how the framework handles exceptions.<br/>
 * <br/>
 * The framework should force the user to write catch clauses for exactly the checked
 * exceptions that are declared to be thrown by the methods called asynchronously.
 */
@RunWith(Parameterized.class)
public class ExceptionTest
{
	@Parameterized.Parameters(name = "exception in tasks: {0}, {1}")
	public static Collection<Object> getParameters() {
		return Arrays.asList(
			new Object[]{ false, false },
			new Object[]{ false, true },
			new Object[]{ true, false },
			new Object[]{ true, true }
		);
	}

	private final boolean throwExceptionInTask1;
	private final boolean throwExceptionInTask2;

	public ExceptionTest(boolean throwExceptionInTask1, boolean throwExceptionInTask2) {
		this.throwExceptionInTask1 = throwExceptionInTask1;
		this.throwExceptionInTask2 = throwExceptionInTask2;
	}

	@Test
	public void testExceptions() {
		try (TaskCoordinator coordinator = TaskCoordinators.createTaskCoordinator()) {
			coordinator.execute(() -> run1(), REGULAR);
			coordinator.execute(() -> run2(), REGULAR);
		} catch (InterruptedException e) {
			Assert.fail("Interrupted exception");
		} catch (Exception1 exception1) {
			Assert.assertTrue("Unexpected exception in task 1", throwExceptionInTask1);
			return;
		} catch (Exception2 exception2) {
			Assert.assertTrue("Unexpected exception in task 2", throwExceptionInTask2);
			return;
		}
		Assert.assertTrue("An exception has been swallowed", !throwExceptionInTask1 && !throwExceptionInTask2);
	}

	private void run1() throws Exception1 {
		TestUtils.sleep(500);
		if (throwExceptionInTask1) {
			throw new Exception1();
		}
	}

	private void run2() throws Exception2 {
		TestUtils.sleep(500);
		if (throwExceptionInTask2) {
			throw new Exception2();
		}
	}

	private static class Exception1 extends Exception {}

	private static class Exception2 extends Exception {}
}
