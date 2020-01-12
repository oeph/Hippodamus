package dd.kms.hippodamus.handles.impl;

import dd.kms.hippodamus.coordinator.ExecutionCoordinator;
import dd.kms.hippodamus.exceptions.StoppableExceptionalCallable;
import dd.kms.hippodamus.handles.ResultHandle;
import dd.kms.hippodamus.logging.LogLevel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DefaultResultHandle<T> extends AbstractHandle implements ResultHandle<T>
{
	private final ExecutorService						executorService;
	private final StoppableExceptionalCallable<T, ?>	callable;
	private Future<T>									futureResult;
	private T											result;

	public DefaultResultHandle(ExecutionCoordinator coordinator, ExecutorService executorService, StoppableExceptionalCallable<T, ?> callable) {
		super(coordinator,  new HandleState(false, false));
		this.executorService = executorService;
		this.callable = callable;
	}

	@Override
	void doSubmit() {
		futureResult = executorService.submit(this::executeCallable);
	}

	@Override
	void doStop() {
		futureResult.cancel(true);
	}

	@Override
	public T get() {
		if (!hasCompleted() && !isCompleting()) {
			// TODO: Nice feature, but maybe there are some use cases where the user just did not know about
			// this dependency and prefers to wait instead?
			getExecutionCoordinator().log(LogLevel.INTERNAL_ERROR, this, "Accessing handle value although it has not completed");
		}
		return result;
	}

	private void setResult(T value) {
		synchronized (state) {
			if (state.isFlagSet(StateFlag.COMPLETED) || state.getException() != null) {
				return;
			}
			result = value;
			markAsCompleted();
		}
	}

	private T executeCallable() {
		try {
			T result = callable.call(this::hasStopped);
			setResult(result);
			return result;
		} catch (Throwable throwable) {
			setException(throwable);
			return null;
		}
	}
}