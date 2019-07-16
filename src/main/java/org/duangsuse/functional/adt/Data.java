package org.duangsuse.functional.adt;

import java.util.Iterator;

/** Data class base */
public abstract class Data<ME extends Data<ME>> extends Object {
  public boolean equalTo(ME other) {
    final Iterator<?> xs = Streams.make(this.productMembers());
    final Iterator<?> ys = Streams.make(other.productMembers());
    Streams.zipWithBreaking(($, x, y) -> !x.equals(y), xs, ys);
    return !(xs.hasNext() || ys.hasNext());
  }

  protected abstract Object[] productMembers();
  
  protected String dataName() { return this.getClass().getName(); }
  protected String memberSeprator() { return ", "; }
  
  protected abstract boolean isInstance(Object other);

  @SuppressWarnings("unchecked")
  @Override public
  boolean equals(Object other) {
   	if (other == null) return false;
    if (!isInstance(other)) return false;
    return this.equalTo((ME) other);
  }

  @Override public
  String toString() {
    final StringBuilder desc = new StringBuilder(dataName());
    Fmt.surround(desc, Fmt.PAREN, () -> Fmt.joinTo(desc, productMembers(), memberSeprator()));
    return desc.toString();
  }
  
  @Override public
  int hashCode() {
    final Iterator<Integer> childs = Streams.map(Object::hashCode, Streams.make(productMembers()));
    return Streams.fold((ac, x) -> ac ^ x, 0x0, childs);
  }
}
