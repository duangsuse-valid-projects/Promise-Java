package org.duangsuse.promise;

import java.util.*;
import java.util.function.*;

import org.duangsuse.functional.Reference;

public class Promise<R> implements Thenable<R> {
	private Queue<FailableHandler<R>> thenChain = new LinkedList<>();
	private Promise.State promiseState = State.PENDING;
	private R res0; private Exception ex0;
	
	public Promise() {}
	Promise(Promise.State initial) {promiseState= initial;}
	Promise(Promise.State initial, R reso) {promiseState= initial;res0= reso;}
	Promise(Promise.State initial, R reso, Exception rej)
	  {promiseState= initial;res0= reso;ex0= rej;}
	
	public State state() {return promiseState;}

	@FunctionalInterface
	public static interface Tasking<RR> {
	  public abstract RR execute(); }
	
	Promise(Tasking<R> singleRes) {
		new Task<R>(this, singleRes).execute();
	}

	public static class Task<T> implements Tasking<T> {
		protected final Tasking<T> task;
		private Promise<T> promise;

		public Task(Promise<T> pr, Tasking<T> task) {promise= pr; this.task= task;}
		public Task(Promise<T> pr, Function<Promise<T>, T> work) {promise= pr; task= () -> work.apply(promise);}
		@SuppressWarnings("unused")
		public Task<T> attach(Promise<T> observ) {promise= observ; return this;}

		protected T executeActual() { return task.execute(); }

		@Override
		public T execute() {
			final T result; try { result = executeActual(); }
			catch (Exception ex) { promise.reject(ex); return null; }
			promise.resolve(result);
			return result;
		}
	}

	public void resolve(final R result)
	  { promiseState=State.RESOLVED; res0= result; thenChain.forEach((it) -> it.accept(result)); }
	public void reject(final Exception ex)
		{ promiseState=State.REJECTED; ex0= ex; thenChain.forEach((it) -> it.rescue(ex)); }

	@Override
	public Promise<R> then(Consumer<? super R> next) {
		if (state() == State.RESOLVED) next.accept(res0);
		thenChain.add(FailableHandler.fromConsumer(next)); return this; }
	@Override
	public Promise<R> then(FailableHandler<R> failable_next) {
		if (state() == State.RESOLVED) failable_next.accept(res0);
		if (state() == State.REJECTED) failable_next.rescue(ex0);
		thenChain.add(failable_next); return this; }

	public void rescue(Consumer<Exception> catcher) {
		if (state() == State.REJECTED) catcher.accept(ex0);
		thenChain.add(FailableHandler.fromCatcher(catcher)); }
	public void rescue(Promise<?> parent)
	  { rescue((ex) -> parent.reject(ex)); }

	public static <T> Promise<T> immediate(T res) { return Promise.begin(() -> res); }
	@SafeVarargs
	public static <T> Promise<T[]> allUnsafe(Promise<T>... promises) {
		@SuppressWarnings("unchecked")
		final T[] results = (T[])new Object[promises.length];
		final Reference.VolatileMut<Integer> count = Reference.VolatileMut.to(0);
		final Promise<T[]> result = new Promise<>();

		for (Promise<T> prm : promises) {
			prm.then((o) -> {
				 results[count.get()] = o;
				 count.assign((x) -> x+1);
				 /*noseq*/ if (count.get() >=results.length) result.resolve(results);
			}).rescue(result);
		}
		return result;
	}

	@SafeVarargs
	public static <T> Promise<T> race(Promise<T>... promises) {;
		final Promise<T> first = new Promise<>();
		for (Promise<T> prm : promises) {
			prm.then(first::resolve).rescue(first);
		}
		return first;
	}

	 public static <T> Promise<T> begin(final Function<Promise<T>, T> work) {
		 Promise<T> prm = new Promise<T>();
		 @SuppressWarnings("unused")
		 T $_ = new Task<T>(prm, work).execute();
		 return prm; }

	 public static <T> Promise<T> begin(final Tasking<T> job) {return begin(($_) -> job.execute());}

	public static enum State {
		PENDING("pending",null), RESOLVED("resolved",PENDING), REJECTED("rejected",PENDING);
		State(String nam, State pred) {name= nam; this.pred= pred;}
		final String name;
		final State pred;
		@Override
		public String toString() {return name;}
	}
	
	@Override
	public String toString()
	{ return "Promise#"+state().toString()+"%("+res0+"/"+ex0+")"+thenChain.size()+"$"
	  +((thenChain.size()<10)? thenChain.toString():thenChain.peek().toString()); }
}
