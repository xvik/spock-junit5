package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.TestInstances;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Represents the extension root context.  This context is the root/parent to {@link ClassContext}
 * and allows for maintaining a store/data spanning all tests within the Spock engine.
 * <p>
 * Based on {@code org.junit.jupiter.engine.descriptor.JupiterEngineExtensionContext} from junit-jupiter-engine.
 *
 * @author Ken Davidson
 * @since 18.11.2022
 */
public class EngineContext extends AbstractContext {

  public EngineContext() {
    super(null, new ExtensionRegistry(null), null, null);
  }

  @Override
  public String getUniqueId() {
    return "[engine:spock]";
  }

  @Override
  public String getDisplayName() {
    return "Spock Engine Context";
  }

  @Override
  public Optional<Object> getTestInstance() {
    return Optional.empty();
  }

  @Override
  public Optional<TestInstances> getTestInstances() {
    return Optional.empty();
  }

  @Override
  public Optional<Method> getTestMethod() {
    return Optional.empty();
  }

  @Override
  public Optional<TestInstance.Lifecycle> getTestInstanceLifecycle() {
    // Lifecycle not available above class level
    return Optional.empty();
  }
}
