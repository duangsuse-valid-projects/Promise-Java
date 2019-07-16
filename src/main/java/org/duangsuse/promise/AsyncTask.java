package org.duangsuse.promise;

import java.util.concurrent.*;
import java.util.function.Function;

import org.duangsuse.promise.Promise.Tasking;

public class AsyncTask<T> extends Promise.Task<T> {
  public static int DEFAULT_PARALLELISM = processors();

  public AsyncTask(Promise<T> pr, Tasking<T> task) { super(pr, task); }
  static <A> Promise<A> launch(final Function<Promise<A>, A> job) {
    Promise<A> promise = new Promise<>();
    new AsyncTask<>(promise, () -> job.apply(promise));
    return promise;
  }
  @Override protected
  T executeActual() { defaultExecutor().execute(() -> task.execute()); return null; }
  
  static Executor defaultExecutor() { return defaultExecutor(true); }
  static Executor defaultExecutor(boolean use_private) {
    try {
      @SuppressWarnings("unchecked")
      Class<ForkJoinPool> poolKlz = (Class<ForkJoinPool>) Class.forName("java.util.concurrent.ForkJoinPool");

      if (use_private) return poolKlz.getDeclaredConstructor(new Class<?>[] {int.class}).newInstance(DEFAULT_PARALLELISM);
      return (Executor) poolKlz.getDeclaredMethod("commonPool", new Class<?>[0]).invoke(null);
    } catch (Exception $_) { return createPlainPool(); }
  }
  static int processors()
    { try { return Runtime.getRuntime().availableProcessors(); }
      catch (Exception $_) { return 2; } }
  static Executor createPlainPool()
    { return Executors.newFixedThreadPool(DEFAULT_PARALLELISM); }
}
