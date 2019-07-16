package org.duangsuse.functional;

import org.junit.Test;

import static org.duangsuse.functional.Maybe.NONE;
import static org.junit.Assert.*;

import org.duangsuse.functional.Maybe;
import org.duangsuse.functional.Maybe.*;

public class MaybeTest {
  @Test(expected = RuntimeException.class)
  public void noneValue() {
    assert !NONE.isSome(): "None is NOT some";
    NONE.take();
    NONE.fmap(($_) -> { fail("fmap(f) Should not call f when NONE given"); return null; });
    NONE.asSome();
  }
  
  @Test
  public void someValue() {
    Some<Integer> x = new Some<Integer>(1);
    assert x.isSome();
    assert x.asSome() == x;
    assert 1 == x.take(): "incorrect take";
    Maybe<Integer> x1 = x.fmap((n) -> n + 1);
    assert x1.isSome();
    assert x1.asSome().take() == 2;
  }
  
  @Test
  public void takeOrWorks() {
    Maybe<Integer> none = Maybe.none();
    assert 1 == none.takeOr(1);
    assert 2 == none.takeOr(() -> Integer.parseInt("2"));
    assert 100 == none.or(new Maybe.Some<Integer>(100)).take();
    Maybe<Integer> some = new Maybe.Some<Integer>(9);
    assert 9 == some.takeOr(1);
    assert 9 == some.takeOr(() -> 2);
    assert 9 == some.or(none).ensure();
  }
  
  @Test
  public void implementFunctor() {
    Maybe<String> maybe = new Maybe.Some<String>("str");
    assert maybe.eta("st").ensure().equals("st");
    assert maybe.etaₓ(1).ensure().equals(1);
    assert maybe.fmap((x) -> x).isSome();
    assert maybe.fmap((s) -> s.charAt(0)).fmap((c) -> c == 's').take();
    assert maybe.flatMap((x) -> Maybe.none()).join() == null;
  }
  
  private void toStringImp() {
    Maybe<Integer> imay = new Some<>(0xcafebabe);
    assert imay.toString().equals("Maybe("+0xcafebabe+")");
  }
  
  private void hashCodeImp() {
    Maybe<Integer> imay = new Some<>(1);
    assert imay.hashCode() == Integer.valueOf(1).hashCode();
    assert new Maybe.None<Integer>().hashCode() == 0;
  }
  
  @Test
  public void dataClassImp() {
    Maybe<Boolean> bmay0 = new Some<>(true);
    Maybe<Boolean> bmay1 = new Some<>(false);
    Maybe<Boolean> non = Maybe.none();
    assert bmay0.equals(bmay0): "reflexive";
    assert !bmay0.equals(bmay1);
    assert !bmay1.equals(bmay0): "symmetric";
    assert !non.equals(bmay0);
    assert !non.equals(bmay1): "transitive";
    assert !bmay1.equals(bmay0);
    assert non.equals(non);
    assert bmay0.equals(bmay0): "consistent";
    toStringImp();
    hashCodeImp();
  }
}
