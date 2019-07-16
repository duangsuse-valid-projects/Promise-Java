package org.duangsuse.functional;
import java.io.Serializable;
import java.util.function.Function;

import org.duangsuse.functional.Messages.S;

/**
 * (Maybe mutable) Reference to existing object
 * @author duangsuse
 * @param <T> Referenced object type
 */
@SuppressWarnings("rawtypes")
public abstract class Reference<T> extends Object implements Cloneable, Serializable {
  private static final long serialVersionUID = 1L;
  public static final Reference<Void> NULL = new Ref<>(null);

  /** Gets referenced object */
  public abstract T get();

  /** Is this object null */
  public boolean isNull() { return get() ==null; }

  /** Override for Functor implementation */
  public T join() { return get(); }

  /** Simple immutable reference */
  public static class Ref<T> extends Reference<T> implements Functor<Ref<T>, Ref, T> {
    private static final long serialVersionUID = 1L;

    /** Actual referenced object */ private final T object;
    public Ref(T x) { object = x; }
    @Override public T get() { return object; }
    public static <T> Ref<T> to(final T x) {return new Ref<>(x);}

    @Override
    public <B> Ref<B> fmap(Function<? super T, B> f)
      { return etaₓ(f.apply(get())); }
    @Override
    public Ref<T> eta(T cat) { return Ref.to(cat); }
    @Override
    public <B> Ref<B> etaₓ(B cat) { return new Ref<B>(cat); }
  }

  /** Mutable reference container */
  public static abstract class Mutable<T> extends Reference<T> {
    private static final long serialVersionUID = 1L;
    /**
     * Sets referenced field
     * @param x new object reference
     */
    public abstract void set(T x);

    /**
     * Apply change to referenced field using operator
     * @param modification map function
     */
    public void assign(Function<T, T> modification)
    { set(modification.apply(get())); }
  }

  public static class Mut<T> extends Mutable<T> implements Functor<Mut<T>, Mut, T> {
    private static final long serialVersionUID = 1L;
    /** Actual referenced object */ private T mutable;
    public Mut(T x) { mutable = x; }
    @Override public T get() { return mutable; }
    @Override public void set(T x) { mutable = (x); }
    public static <T> Mut<T> to(final T x) {return new Mut<>(x);}

    @Override
    public Mut<T> eta(T cat) { return Mut.to(cat); }
    @Override
    public <B> Mut<B> etaₓ(B cat) { return new Mut<B>(cat); }
    public <B> Mut<B> fmap(Function<? super T, B> f)
    { return etaₓ(f.apply(get())); }
  }

  /** Volatile and mutable reference */
  public static class VolatileMut<T> extends Mutable<T> implements Functor<VolatileMut<T>, VolatileMut, T> {
    private static final long serialVersionUID = 3L;
    private volatile T saferef;
    VolatileMut(T x) { saferef = x; }
    @Override public T get() { return saferef; }
    @Override public void set(T x1) { saferef = x1; }
    public static <T> VolatileMut<T> to(final T x) {return new VolatileMut<>(x);}

    @Override
    public VolatileMut<T> eta(T cat) { return VolatileMut.to(cat); }
    @Override
    public <B> VolatileMut<B> etaₓ(B cat) { return new VolatileMut<B>(cat); }
    public <B> VolatileMut<B> fmap(Function<? super T, B> f)
      { return etaₓ(f.apply(get())); }
  }

  @Override
  public String toString() { return "ref"+S.C + get().toString(); }
  @Override
  public boolean equals(Object other) { return get().equals(other); }
  @Override
  public int hashCode() { return get().hashCode(); }
}
