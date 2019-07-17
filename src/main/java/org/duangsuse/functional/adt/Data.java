package org.duangsuse.functional.adt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.*;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;

/** Data class base */
public abstract class Data<ME extends Data<ME>> extends Object {
  final Class<?> $klass = this.getClass().getClass().cast( this.getClass() );

  static volatile boolean metaInitialized = false;
  static Class<?> actual = null;
  static String dataName = null, memberSep = null;
  static Pair<String, String> surround;
  static Field[][] kinds = new Field[3][]; //0=S, 1=E, 2=H

  private static void ensureReflectMetadata(Class<?> klass) {
    if (metaInitialized) return;
    if (klass.isAnnotationPresent(TypedData.class)) {
      TypedData meta = klass.getAnnotation(TypedData.class);
      actual = meta.type();
      dataName = meta.name();
      memberSep = meta.sep();
    } else {
      dataName = klass.getName();
      memberSep = ", ";
    }
    partExclusionMembers(klass);
    surround = Fmt.PAREN;
    metaInitialized = true; // publication-safe
  }

  private static void partExclusionMembers(Class<?> klass) {
    LinkedList<Pair<Field, exclude>> exclusions = new LinkedList<>();
    for (Field f : klass.getFields()) {
      if (f.isAnnotationPresent(exclude.class))
        exclusions.add(new Pair<>(f, f.getAnnotation(exclude.class))); }
    LinkedList<Field> str = new LinkedList<>(), eq = new LinkedList<>(), hash = new LinkedList<>();
    for (Pair<Field, exclude> policy : exclusions) {
      Field f = policy.fst(); exclude rule = policy.snd();
      exclude.FROM kind = rule.kind();

      boolean s = kind.hasFlag(exclude.S), e = kind.hasFlag(exclude.E), h = kind.hasFlag(exclude.H);
      if (!s) str.add(f); if (!e) eq.add(f); if(!h) hash.add(f);
    }
    str.toArray(kinds[0]); eq.toArray(kinds[1]); hash.toArray(kinds[2]);
  }

  protected boolean isInstance(Object other) {
    ensureReflectMetadata($klass);
    if (actual == null) return $klass.isInstance(other);
    return actual.isInstance(other);
  }

  protected abstract Object[] productMembers();

  protected String dataName() { ensureReflectMetadata($klass); return dataName; }
  protected String memberSeprator() { ensureReflectMetadata($klass); return memberSep; }

  protected Object[] equalityMembers() { return kinds[1]; };
  protected Object[] hashedMembers() { return kinds[2]; };
  protected Object[] shownMembers() { return kinds[0]; };

  @Retention(RUNTIME) @Target(TYPE)
  public static @interface TypedData {
    static final String NONE = "(none)";
    abstract Class<?> type();
    abstract String name() default NONE;
    abstract String sep() default NONE;
  }
  @Retention(RUNTIME) @Target(FIELD)
  public static @interface exclude {
    abstract FROM kind() default FROM.EQUALS;

    final static int
      S = 0, E = 1, H = 2;
    static enum FROM {
      STRING (S << 0b1),
      EQUALS (E << 0b1),
      HASH   (H << 0b1),
      SE     (STRING.also(EQUALS)),
      EH     (EQUALS.also(HASH)),
      SH     (STRING.also(HASH));

      protected final int bitFlag;
      FROM(int flag) { this.bitFlag = flag; }
      private final boolean hasFlag(int id)
        { return ((id << 0b1) & bitFlag) != 0; }
      private final int also(final FROM other)
        { return bitFlag & other.bitFlag; }
    }
  }

  ////
  public boolean equalTo(ME other) {
    final Iterator<?> xs = Streams.make(this.equalityMembers());
    final Iterator<?> ys = Streams.make(other.equalityMembers());
    Streams.zipWithBreaking(($, x, y) -> !x.equals(y), xs, ys);
    return !(xs.hasNext() || ys.hasNext());
  }

  @SuppressWarnings("unchecked")
  @Override public
  boolean equals(Object other) {
   	if (other == null) return false;
    if (!isInstance(other)) return false;
    return this.equalTo((ME) other);
  }

  @Override public
  String toString() {
    final StringBuilder desc = Fmt.writer(dataName());
    Fmt.surround(desc, surround, () -> Fmt.joinTo(desc, shownMembers(), memberSeprator()));
    return desc.toString();
  }

  @Override public
  int hashCode() {
    final Iterator<Integer> childs = Streams.map(Object::hashCode, Streams.make(hashedMembers()));
    return Streams.fold((ac, x) -> ac ^ x, 0x0, childs);
  }
}
