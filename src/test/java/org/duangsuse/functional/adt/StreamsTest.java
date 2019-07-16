package org.duangsuse.functional.adt;

import java.util.Iterator;
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
}
