package org.duangsuse.functional.adt;

/** Pair of things */
public class Pair<A, B> {
  private A x; private B y;
  public Pair(A fst, B snd) { x = fst; y = snd; }
  public A fst() { return x; }
  public B snd() { return y; }
  @Override public
  String toString() { return x + "∣" + y; }
  @Override public
  boolean equals(Object other) {
    if (!(other instanceof Pair)) return false;
    Pair<?, ?> pxy = (Pair<?, ?>) other;
    return pxy.fst().equals(this.fst()) && pxy.snd().equals(this.snd());
  }
  @Override public
  int hashCode() { return x.hashCode() ^ y.hashCode(); }
}
