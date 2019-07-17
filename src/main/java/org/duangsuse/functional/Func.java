package org.duangsuse.functional;

import java.util.function.*;

/** Helper functions for functions */
public final class Func {
  public static <T> T id(final T x) { return x; }
  public static <T> Function<T, T> identity() { return Func::id; }

  static <R, X, Y> Func2<R, Y, X> flip2(Func2<R, X, Y> f)
    { return (y, x) -> f.invoke(x, y); }

  static <R, X, Y, Z> Func3<R, Y, X, Z> flip3$0(Func3<R, X, Y, Z> f)
    { return (y, x, z) -> f.invoke(x, y, z); }
  static <R, X, Y, Z> Func3<R, X, Z, Y> flip3$1(Func3<R, X, Y, Z> f)
    { return (x, z, y) -> f.invoke(x, y, z); }

  public static <R> Func0<R> into(Func0<R> f) { return f; }
  @FunctionalInterface
  public static interface Func0<R> extends Supplier<R> {
    abstract R invoke();
    default R get() { return invoke(); }
  }

  public static Funv0 into(Funv0 f) { return f; }
  @FunctionalInterface
  public static interface Funv0 extends Runnable {
    abstract void run();
    default void invoke() { run(); }
  }
  ////

  public static <R, I0> Func1<R, I0> into(Func1<R, I0> f) { return f; }
  @FunctionalInterface
  public static interface Func1<R, I0> extends Function<I0, R> {
    abstract R invoke(I0 x0); // from java.util.function.Function
    default R apply(I0 x0) { return invoke(x0); }
    default Func0<R> curry1(I0 x) { return curry(this).apply(x); }
  }
  static <R, I0> Function<I0, Func0<R>> curry(Func1<R, I0> f)
    { return (x0) -> () -> f.invoke(x0); }

  public static <I0> Funv1<I0> into(Funv1<I0> f) { return f; }
  // 可能不希望任何 capture<? super T> 函数接受 Funv 的结果，于是返回 bottom type Void
  @FunctionalInterface
  public static interface Funv1<I0> extends Function<I0, Void>, Consumer<I0> {
    abstract void invoke(I0 x0);
    default Void apply(I0 x0) { invoke(x0); return null; }
    default void accept(I0 input) { invoke(input); }
    default Funv0 curry1(I0 x) { return () -> curry(this).apply(x).run(); }
  }
  static <I0> Function<I0, Funv0> curry(Funv1<I0> f)
    { return (x0) -> () -> f.invoke(x0); }

  ////
  public static <R, I0, I1> Func2<R, I0, I1> into(Func2<R, I0, I1> f) { return f; }
  @FunctionalInterface
  public static interface Func2<R, I0, I1> {
    abstract R invoke(I0 x0, I1 x1);
    default Func1<R, I1> curry1(I0 x0) { return curry(this).apply(x0); }
    default Func0<R> curry2(I0 x0, I1 x1) { return curry1(x0).curry1(x1); }
  }
  static <R, I0, I1> Function<I0, Func1<R, I1>> curry(Func2<R, I0, I1> f)
    { return (x0) -> (x1) -> f.invoke(x0, x1); }
  
  public static <I0, I1> Funv2<I0, I1> into(Funv2<I0, I1> f) { return f; }
  @FunctionalInterface
  public static interface Funv2<I0, I1> {
    abstract void invoke(I0 x0, I1 x1);
    default Funv1<I1> curry1(I0 x0) { return curry(this).apply(x0); }
    default Funv0 curry2(I0 x0, I1 x1) { return curry1(x0).curry1(x1); }
  }
  static <I0, I1> Function<I0, Funv1<I1>> curry(Funv2<I0, I1> f)
    { return (x0) -> (x1) -> f.invoke(x0, x1); }

  ////
  public static <R, I0, I1, I2> Func3<R, I0, I1, I2> into(Func3<R, I0, I1, I2> f) { return f; }
  @FunctionalInterface
  public static interface Func3<R, I0, I1, I2> {
    R invoke(I0 x0, I1 x1, I2 x2);
    default Func2<R, I1, I2> curry1(I0 x0) { return curry(this).apply(x0); }
    default Func1<R, I2> curry2(I0 x0, I1 x1) { return curry1(x0).curry1(x1); }
  }
  static <R, I0, I1, I2> Function<I0, Func2<R, I1, I2>> curry(Func3<R, I0, I1, I2> f)
    { return (x0) -> (x1, x2) -> f.invoke(x0, x1, x2); }

  public static <I0, I1, I2> Funv3<I0, I1, I2> into(Funv3<I0, I1, I2> f) { return f; }
  @FunctionalInterface
  public static interface Funv3<I0, I1, I2> {
    void invoke(I0 x0, I1 x1, I2 x3);
    default Funv2<I1, I2> curry1(I0 x0) { return curry(this).apply(x0); }
    default Funv1<I2> curry2(I0 x0, I1 x1) { return curry1(x0).curry1(x1); }
  }
  static <I0, I1, I2> Function<I0, Funv2<I1, I2>> curry(Funv3<I0, I1, I2> f)
    { return (x0) -> (x1, x2) -> f.invoke(x0, x1, x2); }
  
  ////
  public static <R, I0, I1, I2, I3> Func4<R, I0, I1, I2, I3> into(Func4<R, I0, I1, I2, I3> f) { return f; }
  @FunctionalInterface
  public static interface Func4<R, I0, I1, I2, I3> {
    R invoke(I0 x0, I1 x1, I2 x2, I3 x3);
    default Func3<R, I1, I2, I3> curry1(I0 x0) { return curry(this).apply(x0); }
  }
  static <R, I0, I1, I2, I3> Function<I0, Func3<R, I1, I2, I3>> curry(Func4<R, I0, I1, I2, I3> f)
    { return (x0) -> (x1, x2, x3) -> f.invoke(x0, x1, x2, x3); }

  ////
  public static <R, I0, I1, I2, I3, I4> Func5<R, I0, I1, I2, I3, I4> into(Func5<R, I0, I1, I2, I3, I4> f) { return f; }
  @FunctionalInterface
  public static interface Func5<R, I0, I1, I2, I3, I4> {
    R invoke(I0 x0, I1 x1, I2 x2, I3 x3, I4 x4);
    default Func4<R, I1, I2, I3, I4> curry1(I0 x0) { return curry(this).apply(x0); }
  }
  static <R, I0, I1, I2, I3, I4> Function<I0, Func4<R, I1, I2, I3, I4>> curry(Func5<R, I0, I1, I2, I3, I4> f)
    { return (x0) -> (x1, x2, x3, x4) -> f.invoke(x0, x1, x2, x3, x4); }
}
