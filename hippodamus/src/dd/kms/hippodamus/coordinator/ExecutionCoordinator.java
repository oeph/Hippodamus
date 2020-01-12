package dd.kms.hippodamus.coordinator;

import dd.kms.hippodamus.exceptions.ExceptionalRunnable;
import dd.kms.hippodamus.handles.Handle;
import dd.kms.hippodamus.logging.LogLevel;

public interface ExecutionCoordinator extends ExecutionManager, AutoCloseable
{
	/**
	 * Call this method to configure how a certain task has to be executed.
	 */
	ExecutionConfigurationBuilder<?> configure();

	/**
	 * Call this method to permit/deny task submission.<br/>
	 * By default, task submission is permitted. In that case, tasks can already be executed although not
	 * all tasks have been registered at the coordinator via {@link #execute(ExceptionalRunnable)}
	 * or related methods. However, the coordinator can only manage exceptions that are raised inside the tasks.
	 * It cannot handle exceptions that are directly raised inside the try-block. If such an exception occurs,
	 * all registered tasks will run to end while the coordinator is closing (because the coordinator does not get
	 * informed about this exception). Only then, the exception will be caught by any exception handler. Hence,
	 * a lot of time may be wasted between throwing and catching the exception.<br/>
	 * You can avoid wasting that time by guarding the code inside the try-block with calls to this method:<br/>
	 * <pre>
	 * try (ExecutionCoordinator coordinator = Coordinators.createExecutionCoordinator()) {
	 *     coordinator.permitTaskSubmission(false);
	 *     // register your tasks here: coordinator.execute(...)
	 *     coordinator.permitTaskSubmission(true);
	 * }
	 * </pre>
	 * <b>Note:</b> When calling {@code permitTaskSubmission(true)}, this does not only permit the submission of
	 * newly registered tasks. Tasks, that are eligible for submission, but have not been submitted yet, will
	 * immediately be submitted.
	 */
	void permitTaskSubmission(boolean permit);

	/**
	 * Stops all tasks created by this service and all of their dependencies.
	 */
	void stop();

	/**
	 * Stops all dependent handles of the specified handle if the handle has been created by this service.
	 */
	void stopDependentHandles(Handle handle);

	void log(LogLevel logLevel, Handle handle, String message);

	@Override
	void close() throws InterruptedException;
}