package org.duangsuse.promise;

import java.util.function.*;

public class ErrorHandler<T> implements FailableHandler<T> {
	private final Consumer<Exception> handler;
	ErrorHandler(Consumer<Exception> proc) {handler =proc;}
	@Override
	public void rescue(Exception ex) {handler.accept(ex);}
	@Override
	public void accept(Object $_) {}
}
