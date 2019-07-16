package org.duangsuse.promise;

import java.util.function.*;

@FunctionalInterface
abstract interface Thenable<R> {
	default Thenable<R> then(Consumer<? super R> next) { then(next); return this; }
	default Thenable<R> done() { then(FailableHandler.fromCatcher(
			FailableHandler.propagateException.apply(this.toString()))); return this; }
	Thenable<R> then(FailableHandler<R> failable); }
