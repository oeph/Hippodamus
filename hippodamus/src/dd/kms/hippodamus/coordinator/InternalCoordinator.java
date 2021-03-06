package dd.kms.hippodamus.coordinator;

import dd.kms.hippodamus.execution.configuration.ExecutionConfigurationBuilder;
import dd.kms.hippodamus.handles.Handle;
import dd.kms.hippodamus.logging.LogLevel;

public interface InternalCoordinator extends ExecutionCoordinator
{
	/**
	 * Stops all dependent handles of the specified handle if the handle has been created by this service.
	 */
	void stopDependentHandles(Handle handle);

	/**
	 * @return the name of the task associated with the {@code handle}. This is either the custom name specified
	 * 			by calling {@link ExecutionConfigurationBuilder#name(String)} before executing the task, or a
	 * 			generic name otherwise.
	 */
	String getTaskName(Handle handle);

	void onCompletion(Handle handle);

	void onException(Handle handle);

	/**
	 * Logs a message for a certain handle at a certain log message.<br/>
	 * <br/>
	 * Ensure that this method is only called with locking the coordinator.
	 */
	void log(LogLevel logLevel, Handle handle, String message);
}
