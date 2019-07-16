package org.duangsuse.functional;
import java.util.ResourceBundle;

public final class Messages {
  private Messages() {}
  private static final String
    BUNDLE_NAME = "org.duangsuse.promise.functional.messages";
  private static final ResourceBundle
    RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  static class S {
    static final String
    L = "(", R = ")",
    BQ = "`", C = ":";
  }

  static class Either {
    static final String
    NAME = "Either",
    ERR = "Err(...)",
    OK = "Ok(...)",
    FAIL = $$("assert_fail");
  }

  static class Maybe {
    static final String
    NAME = "Maybe",
    COERCION = $$("coer"),
    FROM = $$("from"),
    TO = $$("to"),
    VALUE = $$("value");
  }


  public static String $$(final String key) {
    if (RESOURCE_BUNDLE.containsKey(key))
      return RESOURCE_BUNDLE.getString(key);
    return '!' + key + '!';
  }
}
