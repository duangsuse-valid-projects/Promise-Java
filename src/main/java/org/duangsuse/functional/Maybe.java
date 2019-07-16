package org.duangsuse.functional;
import static org.duangsuse.functional.Messages.Maybe.*;

import java.util.function.*;

import org.duangsuse.functional.Messages.S;

/**
 * Maybe: handle values with null possibility
 * @author duangsuse
 *
 * @param <T> Inner class type
 */
@SuppressWarnings("rawtypes")
public abstract class Maybe<T> implements Functor<Maybe<T>, Maybe, T> {
  /** Take the actual value */ public abstract T take();
  /** Is this value Some */ public abstract boolean isSome();
  /** Coerce to Some */ public abstract Some<T> asSome();

  public static final None<Void> NONE = new None<>();
  @SuppressWarnings("unchecked")
  public static <T> None<T> none() { return (None<T>)NONE; }

  /** Nothing case (zero sized, singleton) */
  public static class None<T> extends Maybe<T> {
    @Override public T take() { return null; }
    @Override public boolean isSome() { return false; }
    @Override public Some<T> asSome()
      { throw new RuntimeException(COERCION+FROM+"None"+TO+"Some"+" "+VALUE); }
  };

  /** Has value case */
  public static class Some<TT> extends Maybe<TT> {
    /** Actual object */ private final TT value;
    public Some(TT x) { value = x; }
    @Override public TT take() { return value; }
    @Override public boolean isSome() { return true; }
    @Override public Some<TT> asSome() { return this; }
  }

  /**
   * Take the value, or execute an action, taking its result
   * @param otherwise supplied as result when this is {@code None}
   * @return value or {@code otherwise()}
   */
  public T takeOr(Supplier<T> otherwise)
    { return isSome()? take() : otherwise.get(); }
  /**
   *  Take the value, or take fallback value
   * @param fallback fallback value
   * @return value ?: fallback
   */
  public T takeOr(T fallback)
    { return takeOr(() -> fallback); }
  /**
   * Get first {@code Some<T>} from this, that
   * @param that other maybe
   * @return first {@code Some(...)} from [this, that]
   */
  public Maybe<T> or(Maybe<T> that)
    { return isSome()? this : that; }

  /**
   * Must take the value {@code Some(...)}, or fail with {@code AssertionError}
   * @return value if present
   * @throws AssertionFail if value not present
   * @see java.lang.AssertionFail
   */
  public T ensure()
    { return asSome().take(); }

  @SuppressWarnings("unchecked")
  @Override
  public <R> Maybe<R> fmap(Function<? super T, R> f)
    { return isSome()? etaₓ(f.apply(take())) : (Maybe<R>)NONE; }
  @Override
  public Maybe<T> eta(T cat)
    { return new Some<T>(cat); }
  @Override
  public <B> Maybe<B> etaₓ(B cat)
    { return new Some<B>(cat); }
  @Override
  public T join() { return take(); }

  @Override
  public String toString()
    { return Messages.Maybe.NAME + (isSome()? S.L+take()+S.R : "(-)"); }
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Maybe)) return false;
    Maybe mayb = (Maybe) other;
    return (mayb.isSome() == this.isSome())?
        (!this.isSome())? true : mayb.take().equals(this.take()) : false;
  }
  @Override
  public int hashCode()
    { return isSome()? take().hashCode() : 0; }
}
