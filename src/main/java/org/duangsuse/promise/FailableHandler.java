package org.duangsuse.promise;

import java.util.function.*;

@FunctionalInterface
abstract interface FailableHandler<R> extends Consumer<R> { 
	default void rescue(Exception e) {};
	
	static <R> FailableHandler<R> fromConsumer(Consumer<?super R> src)
	{ return (x) -> src.accept(x); }
	static <R> FailableHandler<R> fromCatcher(Consumer<Exception> handler)
	{ return new ErrorHandler<R>(handler); }
	
	static final String UNCAUGHT_EXCEPTION = "Uncaught exception from promise ";
	static Function<String, Consumer<Exception>> propagateException
	  = (s) -> ((ex) -> { throw new RuntimeException(UNCAUGHT_EXCEPTION + s, ex); });
}
