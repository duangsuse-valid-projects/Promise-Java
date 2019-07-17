package org.duangsuse.functional.adt;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

/** Arbitrary operator on sequences */
public final class Streams {
  static <T> Iterator<T> make(List<T> xs) { return xs.iterator(); }
  static <T> ArrayIter<T> make(T[] xs) { return new ArrayStream<T>(xs); }
  static <T> ListIterator<T> makeDoubleEnded(List<T> xs) { return xs.listIterator(); }
  /** Make using vararg array */@SafeVarargs
  static <T> ArrayIter<T> of(T... xs) { return make(xs); }

  /** Double-ended iterator */
  public static abstract interface DeIter<T> extends Iterator<T> {
    abstract boolean hasPrevious();
    abstract T previous();
    default void movePrevious() { previous(); }
    default void moveNext() { next(); }

    public default void moveNextN(int n)
      { for (; n != 0; --n) { moveNext(); } }
    public default void movePerviousN(int n)
      { for (; n != 0; --n) { movePrevious(); } }

    public default void reset() {
      while (this.hasPrevious()) movePrevious(); }
    public default int countedReset() {
      int counted = 0;
      while (this.hasPrevious())
        { movePrevious(); counted += 1; }
      return counted;
    }
    public default int calculateLength() {
      int nexts = countedReset(); int length = 0;
      while (this.hasNext()) { moveNext(); ++length; }
      this.moveNextN(nexts);
      return length;
    }
    /** Get last next() result */
    public default T getLast() {
      final T current;
      if (!hasNext() && !hasPrevious()) return null;
      if (hasNext() ) {
        current = this.next();
        this.movePrevious();
      } else /*hasPervious*/ {
        current = null;
      }
      return current;
    }
    /** See next() result without moving cursor */
    public default T peekNext() {
      if (!hasNext()) return null;
      T next = this.next();
      this.movePrevious();
      return next;
    }
  }
  /** Array iterator with set(T) function */
  public static abstract interface ArrayIter<T> extends DeIter<T> {
    abstract void setLast(T value);
    public default void mapRemaining(Function<? super T, ? extends T> f) {
      this.forEachRemaining((x) -> setLast(f.apply(x)));
    }
  }

  /**
   * 非常抱歉，但是这个 DeIter 的实现是 inefficient 的<br>
   * 我原来的计划是在 next 和 previous 方法里使用 ++i，这样可能会稍微快一些，理论上少分配一个存储<br>
   * 其实在 double-ended 的时候这导致了一个非常烧脑的问题：next 和 getThis / setThis 方法冲突<br>
   * 这是由于两者对索引变量 pos 的解释方法不同造成的。next 先进行自增移动指针；再获取数据、getThis 则模仿 next 的解释方式，取指针下一项数据而不改变指针<br>
   * 可是他们在混用的时候，由于 pos 的模拟实际上是不完全和不兼容的，它直接拿了『光标下的位置』而不会改变指针，但是 next 却是递增指针，返回『这一项』<br>
   * 会导致 getThis() == next()<br>
   * 不能达到 [1,2,3] getThis() == 2 (pos=0, actual 1)时 next() (pos=1, actual=1) 为 3 的属性。<br> 
   * 为了解决这个问题，我已经花了至少两个小时了。我不能再继续浪费时间在这上面了，所以不得不使用一个临时的变通方法
   * 而这个方法甚至比不用 ++i 还慢... 于是我又创建了非 DeIter 版本的 ArrayStream<br>
   * 算了还是不用那个『优化』了，每次返回用这一项的索引。
   * <br>
   * 盖棺定论：到底还是我错了<br>
   * 开始的时候莫名想在程序里加是不是首项目或者末项目的判断，其实是有原因的<br>
   * 因为同时存在 next 和 this 在有限长度的情况下根本就是不可能的，当 next 到第一项(0)的时候没有 this、
   * this 到最后一项的时候没有 next。我开始有这种想法就是因为存在错误，但当时我就感觉到了这是不对的，因为存在两个缝隙不能填补。
   * 
   * <br>
   * 总而言之我对零基数组的索引还需要练习模拟能力。
   * 
   * @author duangsuse
   *
   * @param <T> Element type
   */
  public static class ArrayStream<T> implements ArrayIter<T> {
    private final T[] xs;
    private volatile int pos = -1;
    private final transient int lastPos;
    //private volatile transient boolean fixIndex = false;
    ArrayStream(T[] xs) { this.xs = xs; lastPos = xs.length -1; }

    @Override public
    boolean hasNext() { return !(pos == lastPos || pos > lastPos); }
    @Override public
    T next() { moveNext(); return xs[pos]; }
    @Override public
    boolean hasPrevious() { return pos >= 1; }
    @Override public
    T previous() { if (pos == lastPos) --pos; movePrevious(); return xs[pos+1]; }

    @Override public
    void moveNext() { pos += 1; }
    @Override public
    void movePrevious() { pos -= 1; }
    @Override public
    void reset() { pos = -1; }
    @Override public
    int calculateLength() { return xs.length; }

    //void indexNeedFix() { fixIndex = true; }
    //void indexFixed() { fixIndex = false; }
    //boolean fixIndex(int offset) {
    //  if (fixIndex) { pos += offset; indexFixed();
    //    return true; } return false; }

    @Override public
    T getLast() { return xs[(pos == lastPos)? lastPos : pos+1]; }
    @Override public
    void setLast(T value) { xs[(pos == lastPos)? lastPos : pos+1] = value; }
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
