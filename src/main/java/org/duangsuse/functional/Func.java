package org.duangsuse.functional;

import java.util.function.*;

/** Helper functions for functions */
public final class Func {
  public static <T> T id(final T x) { return x; }
  public static <T> Function<T, T> identity() { return Func::id; }

  static <R, X, Y> Func2<R, Y, X> flip(Func2<R, X, Y> f)
    { return (y, x) -> f.apply(x, y); }

  @FunctionalInterface
  public static interface Func0<R> { R apply(); }

  @FunctionalInterface
  public static interface Func1<R, I0> { R apply(I0 x0); }
  static <R, I0> Function<I0, Func0<R>> curry(Func1<R, I0> f)
    { return (x0) -> () -> f.apply(x0); }
  @FunctionalInterface
  public static interface Funv1<I0> { void apply(I0 x0); }
  static <I0> Function<I0, Runnable> curry(Funv1<I0> f)
    { return (x0) -> () -> f.apply(x0); }

  @FunctionalInterface
  public static interface Func2<R, I0, I1> { R apply(I0 x0, I1 x1); }
  static <R, I0, I1> Function<I0, Func1<R, I1>> curry(Func2<R, I0, I1> f)
    { return (x0) -> (x1) -> f.apply(x0, x1); }
  @FunctionalInterface
  public static interface Funv2<I0, I1> { void apply(I0 x0, I1 x1); }
  static <I0, I1> Function<I0, Funv1<I1>> curry(Funv2<I0, I1> f)
    { return (x0) -> (x1) -> f.apply(x0, x1); }

  @FunctionalInterface
  public static interface Func3<R, I0, I1, I2> { R apply(I0 x0, I1 x1, I2 x2); }
  static <R, I0, I1, I2> Function<I0, Func2<R, I1, I2>> curry(Func3<R, I0, I1, I2> f)
    { return (x0) -> (x1, x2) -> f.apply(x0, x1, x2); }
  @FunctionalInterface
  public static interface Funv3<I0, I1, I3> { void apply(I0 x0, I1 x1, I3 x3); }
  static <I0, I1, I2> Function<I0, Funv2<I1, I2>> curry(Funv3<I0, I1, I2> f)
    { return (x0) -> (x1, x2) -> f.apply(x0, x1, x2); }
  
  @FunctionalInterface
  public static interface Func4<R, I0, I1, I2, I3> { R apply(I0 x0, I1 x1, I2 x2, I3 x3); }
  static <R, I0, I1, I2, I3> Function<I0, Func3<R, I1, I2, I3>> curry(Func4<R, I0, I1, I2, I3> f)
    { return (x0) -> (x1, x2, x3) -> f.apply(x0, x1, x2, x3); }

  @FunctionalInterface
  public static interface Func5<R, I0, I1, I2, I3, I4> { R apply(I0 x0, I1 x1, I2 x2, I3 x3, I4 x4); }
  static <R, I0, I1, I2, I3, I4> Function<I0, Func4<R, I1, I2, I3, I4>> curry(Func5<R, I0, I1, I2, I3, I4> f)
    { return (x0) -> (x1, x2, x3, x4) -> f.apply(x0, x1, x2, x3, x4); }
}
