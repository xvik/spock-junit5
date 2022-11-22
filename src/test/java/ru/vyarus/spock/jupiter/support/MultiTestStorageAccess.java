package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MultiTestStorageAccess implements BeforeAllCallback, BeforeEachCallback {
  private static final String ROOT_VALUE = "root_value";
  private static final String CLASS_VALUE = "class_value";
  private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ROOT_VALUE);

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    int counter = 0;

    ExtensionContext.Store store = context.getRoot().getStore(NAMESPACE);
    if (store.get(ROOT_VALUE) == null) {
      store.put(ROOT_VALUE, ++counter);
    }
    ActionHolder.add("Root value " + store.get(ROOT_VALUE));

    context.getStore(NAMESPACE).put(CLASS_VALUE, ++counter);
    ActionHolder.add(context.getDisplayName() + " class value " + context.getStore(NAMESPACE).get(CLASS_VALUE));
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    int counter = (int) context.getStore(NAMESPACE).get(CLASS_VALUE);
    ActionHolder.add(context.getDisplayName() + " method value " + ++counter);
  }
}
