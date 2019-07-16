package org.duangsuse.functional;
import static org.duangsuse.functional.Messages.S;
import static org.duangsuse.functional.Messages.Either.*;

import java.util.function.Function;

/**
 * Simple data object of <code>T</code> with two variants:
 * <ul>
 *   <li>Left&lt;T&gt;
 *   <li>Right&lt;T&gt;
 * <br>defined likely Either in Haskell
 * <br>Operators: mapLeft/Right, flatMapLeft/Right,
 *   either, swap, isLeft/Right, asLeft/Right, mustAsLeft/Right
 */
@SuppressWarnings("rawtypes")
public abstract class Either<A, B> implements Functor<Either<A,B>, Either, A> {
  @SuppressWarnings("unchecked")
  @Override public <A1> Either<A1, B> fmap(Function<? super A, A1> f)
    { return (Either<A1, B>) mapLeft((Function<A, A1>) f); }
  @SuppressWarnings("unchecked")
  @Override public <A1> Either<A1, B> flatMap(Function<? super A, Functor<?, Either, A1>> f)
    { return flatMapLeft((Function<? super A, A1>) f); }

  public abstract Object get();

  public abstract boolean isLeft();
  public boolean isRight() {return !isLeft();};

  @SuppressWarnings("unchecked")
  public A asLeft()
    { return(A) (isLeft()? get() : null); };
  @SuppressWarnings("unchecked")
  public B asRight()
    { return(B) (isRight()? get() : null); };

  public <A1> Either<A1, B> flatMapLeft (Function<?super A, A1> f)
    { return etaₓ(f.apply(asLeft())); }
  public <B1> B1 flatMapRight(Function<?super B, B1> f)
    { return f.apply(asRight()); }

  public <A1> Either<A1, Void> mapLeft (Function<A, A1> f)
    { return new Left<A1>(f.apply(asLeft())); }
  public <B1> Either<Void, B1> mapRight(Function<B, B1> f)
    { return new Right<B1>(f.apply(asRight())); }

  public A mustAsLeft() {return checkLeft.apply(asLeft());}
  public B mustAsRight() {return checkRight.apply(asRight());};

  @SuppressWarnings("unchecked")
  @Override public Either<A,B> eta(A x) { return(Either<A,B>) new Left<A>(x); }
  @Override public A join() { return asLeft(); }
  @SuppressWarnings("unchecked")
  @Override public <BB> Either<BB, B> etaₓ(BB cat) { return(Either<BB, B>) new Left<BB>(cat); }

  @SuppressWarnings("unchecked")
  private Function<Object, A>
    checkLeft = (Function<Object,A>)ensureNotNull(Messages.Either.FAIL+ERR);
  @SuppressWarnings("unchecked")
  private Function<Object, B>
    checkRight = (Function<Object,B>)ensureNotNull(Messages.Either.FAIL+OK);

  private static Function<Object, ?> ensureNotNull(final String message)
    { return (x) -> { assert x !=null: message; return x; }; }

  @SuppressWarnings("unchecked")
  public <A1, B1> Either<A1, B1> either(Function<A, A1> f, Function<B, B1> g) {
    return(Either<A1,B1>) (isLeft()? mapLeft(f) : mapRight(g)); }

  @SuppressWarnings("unchecked")
  public Either<B, A> swap()
    { return(Either<B,A>) either((l) -> new Right<A>(l), (r) -> new Left<B>(r)); }

  ////

  public static class Left<T> extends Either<T, Void> {
    private final T value;
    public Left(T x) {value= x;}

    @Override public Object get() {return value;}
    @Override public boolean isLeft() {return true;}
  }

  public static class Right<T> extends Either<Void, T> {
    private final T value;
    public Right(T x) {value= x;}
    @Override public boolean isLeft() {return false;}
    @Override public Object get() {return value;}
  }
  @Override
  public String toString() {
    return Messages.Either.NAME+S.L+(isLeft()? "L":"R")+S.BQ+get().toString()+S.R; }
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Either)) return false;
    Either othereit = ((Either)other);
    return (othereit.isLeft() == this.isLeft())? othereit.get().equals(this.get()) : false; }
  @Override
  public int hashCode() {
    return (isLeft()? 0 : 1) & get().hashCode(); } 
}
