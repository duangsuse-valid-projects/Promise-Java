package org.duangsuse.functional.adt;

import java.util.Iterator;

/** Things related to text formatting */
public final class Fmt {
  static final String
    BQUOTE = "`",
    DQUOTE = "\"",
    QUOTE = "'",
    AT = "@",
    DOLLAR = "$",
    PERCENT = "%",
    AND = "&",
    COLON = ":",
    SEMI = ";";
  static final Pair<String, String>
    PAREN = surronder("( )"),
    BRACKET = surronder("< >"),
    SQUARE = surronder("[ ]"),
    BRACE = surronder("{ }"),
    DESCRIBE = surronder("` "+DQUOTE);

  static Pair<String, String> surronder(final String desc) {
    assert desc.length() == 3: "Must in format <l>.<r>";
    return new Pair<String, String>(String.valueOf(desc.charAt(0)), String.valueOf(desc.charAt(2)));
  }
  static StringBuilder writer() { return new StringBuilder(); }
  static StringBuilder writer(String init) { return new StringBuilder(init); }
  /** Remove last N characters from end of a {@link java.lang.StringBuilder}, do nothing if {@code n > sb.length()} */
  static void deleteLastN(StringBuilder sb, int n) {
    if (sb.length() < n) return;
    int lastIndex = sb.length() -1,
        offset = n -1;
    sb.delete(lastIndex - offset, lastIndex+1);
  }

  static void joinTo(StringBuilder wrt, Iterator<?> stream, final String sep) {
    stream.forEachRemaining((it) -> wrt.append(it.toString()).append(sep));
    deleteLastN(wrt, sep.length());
  }
  static void joinTo(StringBuilder wrt, Object[] ary, final String sep)
    { joinTo(wrt, Streams.make(ary), sep); }
  
  static void surround(StringBuilder sb, Pair<String, String> surrounder, Runnable action) {
    sb.append(surrounder.fst());
    action.run();
    sb.append(surrounder.snd());
  }
  static void surround(StringBuilder sb, String surrounder, Runnable action) {
    sb.append(surrounder); action.run(); sb.append(surrounder);
  }
}