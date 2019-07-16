package org.duangsuse.functional.adt;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/** Arbitrary operator on sequences */
public final class Streams {
  static <T> Iterator<T> make(T[] xs) { return new ArrayStream<T>(xs); }
  @SafeVarargs
  static <T> Iterator<T> of(T... xs) { return make(xs); }

  public static class ArrayStream<T> implements Iterator<T> {
    private final T[] xs;
    private volatile int pos = 0;
    private final int overLast;
    ArrayStream(T[] xs) { this.xs = xs; overLast = xs.length; }
    @Override public
    boolean hasNext() { return pos != overLast; }
    @Override public
    T next() { return xs[pos++]; }
  }

  static <T, A, B> Iterator<T> zipWith(Zipper<? extends T, ? super A, ? super B> f, final Iterator<A> xs, final Iterator<B> ys) {
    return new Iterator<T>() {
      @Override public
      boolean hasNext() { return xs.hasNext() && ys.hasNext(); }
      @Override public
      T next() { return f.zip(xs.next(), ys.next()); }
    };
  }

  static <T, A, B> Iterator<T> zipWithBreaking(EffectZipper<T, ? super A, ? super B> f, final Iterator<A> xs, final Iterator<B> ys) {
    return new Iterator<T>() {
      private volatile boolean broken = false;
      private volatile T current;
      @Override public
      boolean hasNext() { return !broken && xs.hasNext() && ys.hasNext(); }
      @Override public
      T next() {
        if (f.zip((o) -> { current = o; }, xs.next(), ys.next())) broken = true;
        return current;
      }
    };
  }
  
  static <T> List<T> collect(Iterator<T> stream) {
    final List<T> bag = new LinkedList<>();
    stream.forEachRemaining(bag::add);
    return bag;
  }

  @SuppressWarnings("unchecked")
  static <T> T[] collect2Ary(Iterator<T> stream, Class<T[]> 唉) {
    final List<T> bag = new ArrayList<>();
    Class<T> 唉辣鸡Java = (Class<T>) 唉.getComponentType();
    stream.forEachRemaining((x) -> bag.add(唉辣鸡Java.cast(x)));
    try {
      T[] xs = (T[]) java.lang.reflect.Array.newInstance(唉辣鸡Java, new int[] {bag.size()});
      bag.toArray(xs); return xs;
    } catch (IllegalArgumentException|ClassCastException e) { e.printStackTrace(); }
    return null;
  }

  static <T, R> Iterator<R> map(Function<T, R> f, final Iterator<T> xs) {
    return new Iterator<R>() {
      @Override public
      boolean hasNext() { return xs.hasNext(); }
      @Override public
      R next() { return f.apply(xs.next()); }
    };
  }
  
  static <R, X> R fold(Folder<R, ? super X> f, final R right_most, Iterator<X> xs) {
    R accumlated = right_most;
    while (xs.hasNext())
      { accumlated = f.fold(accumlated, xs.next()); }
    return accumlated; }
  
  @FunctionalInterface
  static interface Folder<R, X> { R fold(R left, final X item); }

  /** ZipWith join(x,y) function */
  @FunctionalInterface
  static interface Zipper<R, A, B> { abstract R zip(A x, B y); }
  /** ZipWith for breakingZip using side effects */
  @FunctionalInterface
  static interface EffectZipper<R, A, B>
    { /** true to break */ abstract boolean zip(Consumer<R> outs, A x, B y); } 
}
