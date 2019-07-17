package org.duangsuse.functional.adt;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Arrays;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.duangsuse.functional.adt.Streams.*;

import org.duangsuse.functional.Func;
import org.duangsuse.functional.Reference;
import org.junit.Test;

public class StreamsTest {
  @Test public void
  arrayStream() {
    Iterator<Integer> ints = make(new Integer[] {1,2,3,4});
    assert ints.hasNext();
    assert ints.next() == 1;
    Reference.Mut<Integer> x = Reference.Mut.to(2);
    ints.forEachRemaining((x_) -> { assert x.get() == x_; x.assign((n) -> n + 1); });
    assert !ints.hasNext();
    Iterator<Integer> none = make(new Integer[0]);
    assert !none.hasNext();
    Iterator<Integer> ys = make(new Integer[] {1});
    assert ys.next() == 1;
    assert !ys.hasNext();
  }
  @Test public void
  collector() {
    final String ddf = "Deep dark fantasy♂";
    Iterator<String> dark = Streams.of(ddf.split(" "));
    String[] darkest = collect2Ary(dark, String[].class);
    Iterator<String> dark_ = make(ddf.split(" "));
    assertArrayEquals(new String[] {"Deep", "dark", "fantasy♂"}, darkest);
    assertArrayEquals(collect(dark_).toArray(), darkest);
  }
  
  @Test public void
  zipWithAndMap() {
    final Iterator<String> titlenames = Streams.of("他改变了中国", "毛泽东语录", "三个代表重要思想");
    Iterator<String> titles = map((n) -> "《"+n+"》", titlenames);
    final Iterator<String> authors = Streams.of("华莱士", "毛泽东", "国务院");
    Iterator<String> briefs = Streams.zipWith((t, a) -> a + t, titles, authors);
    assertArrayEquals("华莱士《他改变了中国》;毛泽东《毛泽东语录》;国务院《三个代表重要思想》".split(";"), collect2Ary(briefs, String[].class));
    Iterator<Object> simple = Streams.zipWith((Zipper<Object, Number, Number>)(x, y) -> x, of(1,2,3), of(4,5,6));
    assertArrayEquals(collect2Ary(simple, Object[].class), new Object[] {1,2,3});
  }
  
  @Test public void
  zipWithBreaking() {
    final Iterator<Integer> ints = of(1, 3, 6);
    final Iterator<String> strs = of("一", "三", "六");
    Iterator<String> res = Streams.zipWithBreaking((o, a, b) -> { o.accept(a.toString() + b); return false; }, ints, strs);
    assertArrayEquals(new String[] {"1一", "3三", "6六"}, collect2Ary(res, String[].class));
    Iterator<Integer> s3 = Streams.zipWithBreaking((o, a, b) -> { o.accept(a + b); return a==4; }, of(2,4,6), of(1,3,6));
    assert s3.hasNext();
    assert s3.next() == 3;
    assert s3.next() == 7;
    assert !s3.hasNext();
  }

  @Test public void
  foldOperator() {
    java.util.Map<String, String> translations = new java.util.HashMap<>();
    Func.Funv2<String, String> $ = translations::put;
    $.apply("too", "太"); $.apply("young", "年轻"); $.apply("simple", "简单"); 
    Iterator<String> parts = of("Too", " young ", "too", " simple");
    Function<String, String> translate = (s) -> translations.getOrDefault(s.trim().toLowerCase(), s);
    Iterator<String> translatedparts = Streams.map(translate, parts);
    StringBuilder translated = fold((sb, x) -> sb.append(x).append(" "), new StringBuilder(), translatedparts);
    Fmt.deleteLastN(translated, 1);
    assertEquals("太 年轻 太 简单", translated.toString());
  }

  @Test public void
  doubleEnded() {
    ListIterator<String> strs = makeDoubleEnded(Arrays.asList(new String[] {"a", "b", "c"}));
    Streams.ArrayIter<String> strs1 = of("a,b,c".split(","));
    assertArrayEquals("a,b,c".split(","), collect(strs1).toArray());
    Object[] ary0 = collect(strs).toArray();
    strs1.reset();
    Runnable check = () -> assertArrayEquals(ary0, collect(strs1).toArray());
    check.run();
    assert strs.hasPrevious();
    assertEquals("c", strs1.getLast());
    assertEquals("b", strs1.previous());
    assertEquals("a", strs1.previous());
    assertEquals("a", strs1.peekNext());
    check.run();
    while (strs.hasPrevious()) strs.previous();
    assert strs1.calculateLength() == 3;
    assert strs1.hasPrevious();
    strs1.reset();
    check.run();
    strs1.reset(); // p-1, 0
    assertEquals("a", strs1.getLast()); // p-1, 0
    assertEquals("a", strs1.getLast()); // p-1, 0
    strs1.moveNext(); // p0, 1
    assertEquals("b", strs1.getLast()); // p0, 1, +1, get
    assertEquals("b", strs1.next()); // p0->p1, 1, ++, gets
    strs1.movePrevious();
    assertEquals("b", strs1.getLast());
    assertEquals("b", strs1.peekNext());
    strs1.setLast(".");
    assertEquals(3, strs1.calculateLength());
    assertEquals(".", strs1.getLast());
  }
  @Test public void
  arrayIter() {
    ArrayIter<String> 蛤 = Streams.of("小熊维尼", "红孩儿");
    蛤.reset();
    蛤.moveNext();
    蛤.setLast("?你太美");
    蛤.movePrevious();
    assertEquals(2, 蛤.calculateLength());
    assert 蛤.hasNext();
    assertEquals("小熊维尼", 蛤.next());
    assertEquals("?你太美", 蛤.next());
    assertEquals("?你太美", 蛤.getLast());
    assert !蛤.hasNext();
    蛤.reset();
    assert 蛤.next().endsWith("尼");
    蛤.mapRemaining((s) -> s.replace("?", "鸡"));
    蛤.reset(); 蛤.moveNextN(2);

    assertEquals("鸡你太美", 蛤.getLast()); // oh babe
    蛤.reset();

    assertArrayEquals(new String[] {"小熊维尼", "鸡你太美"}, collect2Ary(蛤, String[].class));
  }
}
