# spock-junit5

[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![CI](https://github.com/xvik/spock-junit5/actions/workflows/CI.yml/badge.svg)](https://github.com/xvik/spock-junit5/actions/workflows/CI.yml)
[![Appveyor build status](https://ci.appveyor.com/api/projects/status/github/xvik/spock-junit5?svg=true)](https://ci.appveyor.com/project/xvik/spock-junit5)
[![codecov](https://codecov.io/gh/xvik/spock-junit5/branch/master/graph/badge.svg)](https://codecov.io/gh/xvik/spock-junit5)

### About

Junit 5 (jupiter) [extensions](https://junit.org/junit5/docs/current/user-guide/#extensions) integration
for [Spock Framework](https://spockframework.org/) (2.x)

Features:

* Supports almost all junit extension types. Problem may appear only with extensions requiring `TestInstanceFactory`
  which is impossible to support because spock manage test instance itself.
    - Warning message would indicate not supported extension types (if usage detected)
* Supports all the same registration types:
    - @ExtendWith on class, method, field or param
    - @RegisterExtension on static and usual fields
* Supports parameters injection in test and fixture methods
* Support junit ExecutionConditions (for example, junit `@Disabled` would work)
* Implemented as global spock extension (implicit activation)
* Additional API for accessing values storage by spock extensions
  (to access values stored by junit extensions or for direct usage because spock does not provide such feature)

Extensions behaviour is the same as in jupiter engine (the same code used where possible). Behavior in both engines
validated with tests.

#### Motivation

Originally developed for [dropwizard-guicey](https://github.com/xvik/dropwizard-guicey) to avoid maintaining special
spock extensions.

Spock 1 provides spock-junit4 module to support junit 4 rules. At that time spock extensions model was light years ahead
of junit. But junit 5 extensions are nearly equal in power to spock extensions (both has pros and cons). It is a big
loss for spock to ignore junit5 extensions:
junit extensions are easier to write, but spock is still much better for writing tests.

### Setup

[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/spock-junit5.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/spock-junit5)

Maven:

```xml

<dependency>
    <groupId>ru.vyarus</groupId>
    <artifactId>spock-junit5</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'ru.vyarus:spock-junit5:1.0.0'
```

##### Snapshots

You can use snapshot versions through [JitPack](https://jitpack.io):

* Go to [JitPack project page](https://jitpack.io/#xvik/spock-junit5)
* Select `Commits` section and click `Get it` on commit you want to use (top one - the most recent)
* Follow displayed instruction: add repository and change dependency (NOTE: due to JitPack convention artifact group
  will be different)

### Usage

Junit 5 extensions could be
applied [as usual](https://junit.org/junit5/docs/current/user-guide/#extensions-registration):

```groovy
@ExtendWith([Ext0, Ext1])
class Test extends Specification {

    @ExtendWith(Ext2)
    static Integer field1

    @ExtendWith(Ext3)
    Integer field2

    void setupSpec(@ExtendWith(Ext4) Integer arg) {
        // ...
    }

    // same for setup method    

    @ExtendWith(Ext5)
    def "Sample test"(@ExtendWith(Ext6) Integer arg) {
        // ...
    }
}
```

All these `@ExtendWith` would be found and registered.

#### Custom annotations

Same as in junit, custom annotations could be used instead of direct `@ExtendWith`:

```groovy
@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith([Ext0, Ext1])
@interface MyExt {}

@MyExt
class Test extends Specification {
    // ...
}
```

#### Programmatic registration

[Programmatic registration](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic) is
also te same:

```groovy
class Test extends Specification {
    
    @RegisterExtension
    static Ext1 ext = new Ext1()

    @RegisterExtension
    Ext2 ext2 = new Ext2()
}
```

NOTE: there is no additional support for `@Shared` spock fields - they would be treated as instance-level extensions (
shared marker just ignored). Junit has value storages, so extension could be static and store and retrieve state from
test-specific storage instead of managing it inside extensions. That's the problem shared state was intended to solve
for spock extensions.

#### Method parameters

As in junit, [`ParameterResolver`](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dependency-injection)
extensions could inject parameters into fixture and test method arguments (constructor is not supported because spock
does not allow constructors usage):

```groovy
@ExtendWith(ParameterExtension)
class Test extends Specification {

    void setupSpec(Integer arg) { ... }
    void cleanupSpec(Integer arg) { ... }
    void setup(Integer arg) { ... }
    void cleanup(Integer arg) { ... }

    def "Sample test"(Integer arg) { ... }
}
```

If extension implemented like this:

```java
public class ParameterExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Integer.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        return 11;
    }
}
```

In all spock methods argument would be set to 11.

Parameter resolution would be executed only on arguments marked as `MethodInfo.MISSING_ARGUMENT`,
so [data-iteration usage](https://spockframework.org/spock/docs/2.0/data_driven_testing.html#data-tables)
would not be a problem:

```groovy
class Test extends Specification {

    @ExtendWith(ParameterExtension.class)
    def "Sample test"(int a, Integer arg) {

        when:
        printn("iteration arg $a, junit arg $arg");

        then:
        true

        where:
        a | _
        1 | _
        2 | _
    }
}
```

Here only `arg` parameter would be resolved with junit extension.

NOTE: if junit extension will not be able to resolve parameter - it will remain as `MethodInfo.MISSING_ARGUMENT`
and subsequent spock extension could insert correct value (junit extensions not own arguments processing).

#### What is supported

Supported:

* ExecutionCondition
* BeforeAllCallback
* AfterAllCallback
* BeforeEachCallback
* AfterEachCallback
* BeforeTestExecutionCallback
* AfterTestExecutionCallback
* ParameterResolver
* TestInstancePostProcessor
* TestInstancePreDestroyCallback
* TestExecutionExceptionHandler

Not supported:

* TestTemplateInvocationContextProvider - junit specific feature (no need for support)
* TestInstanceFactory - impossible to support because spock does not delegate test instance creation
* LifecycleMethodExecutionExceptionHandler - could be supported, but it is very specific
* InvocationInterceptor - same (very specific)
* TestWatcher - no need in context of spock

Of course, constructor parameters injection is not supported because spock does not allow spec constructors.

### Lifecycle

Junit extensions support is implemented as global spock extensions and so it would be executed before any other
annotation-driven spock extension. 

The following code shows all possible fixture methods and all possible interceptors
available for extension (from [spock docs](https://spockframework.org/spock/docs/2.0/extensions.html#_interceptors))

```groovy
@ExtendWith(JunitExtension)
@SpockExtension
class SpockLifecyclesOrder extends Specification {
    
    // fixture methods
    
    void setupSpec() { ... }
    void cleanupSpec() { ... }
    void setup() { ... }
    void cleanup() { ... }

    // feature
    
    def "Sample test"() { ... }
}

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD, ElementType.FIELD])
@ExtensionAnnotation(SpockExtension)
@interface SpockExtension {
    String value() default "";
}

class SpockExtension implements IAnnotationDrivenExtension<SpockLifecycle> {
    @Override
    void visitSpecAnnotation(SpockLifecycle annotation, SpecInfo spec) {
        spec.addSharedInitializerInterceptor new I('shared initializer')
        spec.sharedInitializerMethod?.addInterceptor new I('shared initializer method')
        spec.addInterceptor new I('specification')
        spec.addSetupSpecInterceptor new I('setup spec')
        spec.setupSpecMethods*.addInterceptor new I('setup spec method')
        spec.allFeatures*.addInterceptor new I('feature')
        spec.addInitializerInterceptor new I('initializer')
        spec.initializerMethod?.addInterceptor new I('initializer method')
        spec.allFeatures*.addIterationInterceptor new I('iteration')
        spec.addSetupInterceptor new I('setup')
        spec.setupMethods*.addInterceptor new I('setup method')
        spec.allFeatures*.featureMethod*.addInterceptor new I('feature method')
        spec.addCleanupInterceptor new I('cleanup')
        spec.cleanupMethods*.addInterceptor new I('cleanup method')
        spec.addCleanupSpecInterceptor new I('cleanup spec')
        spec.cleanupSpecMethods*.addInterceptor new I('cleanup spec method')
        spec.allFixtureMethods*.addInterceptor new I('fixture method')
    }

    static class I implements IMethodInterceptor { ... }
}
```

| Junit (context type)               | Spock                      | Kind                | Registration                                    |
|------------------------------------|----------------------------|---------------------|-------------------------------------------------|
|                                    | annotation extensions init |                     | IAnnotationDrivenExtension (all methods)        |
|                                    | shared initializer         | SHARED_INITIALIZER  | spec.addSharedInitializerInterceptor            | 
|                                    | specification              | SPEC_EXECUTION      | spec.addInterceptor                             |
| BeforeAllCallback (c)              |                            |                     |                                                 |
|                                    | setup spec                 | SETUP_SPEC          | spec.addSetupSpecInterceptor                    |
|                                    | setup spec method          | SETUP_SPEC          | spec.setupSpecMethods*.addInterceptor           |
|                                    | fixture method             | SETUP_SPEC          | spec.allFixtureMethods*.addInterceptor          |
|                                    | **TEST setupSpec**         |                     |                                                 |
|                                    | initializer                | INITIALIZER         | spec.addInitializerInterceptor                  |
| TestInstancePostProcessor (c)      |                            |                     |                                                 |
|                                    | feature                    | FEATURE_EXECUTION   | spec.allFeatures*.addInterceptor                |
|                                    | iteration                  | ITERATION_EXECUTION | spec.allFeatures*.addIterationInterceptor       |
| BeforeEachCallback (m)             |                            |                     |                                                 |
|                                    | setup                      | SETUP               | spec.addSetupInterceptor                        |
|                                    | setup method               | SETUP               | spec.setupMethods*.addInterceptor               |
|                                    | fixture method             | SETUP               | spec.allFixtureMethods*.addInterceptor          | 
|                                    | **TEST setup**             |                     |                                                 |
| BeforeTestExecutionCallback (m)    |                            |                     |                                                 |
|                                    | feature method             | FEATURE             | spec.allFeatures*.featureMethod*.addInterceptor |
|                                    | ** TEST body**             |                     |                                                 |
| AfterTestExecutionCallback (m)     |                            |                     |                                                 |
|                                    | cleanup                    | CLEANUP             | spec.addCleanupInterceptor                      |
|                                    | cleanup method             | CLEANUP             | spec.cleanupMethods*.addInterceptor             |
|                                    | fixture method             | CLEANUP             | spec.allFixtureMethods*.addInterceptor          |
|                                    | **TEST cleanup**           |                     |                                                 |
| AfterEachCallback (m)              |                            |                     |                                                 |
| TestInstancePreDestroyCallback (m) |                            |                     |                                                 |
|                                    | cleanup spec               | CLEANUP_SPEC        | spec.addCleanupSpecInterceptor                  |
|                                    | cleanup spec method        | CLEANUP_SPEC        | spec.cleanupSpecMethods*.addInterceptor         |
|                                    | fixture method             | CLEANUP_SPEC        | spec.allFixtureMethods*.addInterceptor          
|                                    | **TEST cleanupSpec**       |                     |                                                 |
| AfterAllCallback (c)               |                            |                     |                                                 |

Kind is a `IMethodInvocation.getMethod().getKind()`. It is shown in case if you use `AbstractMethodInterceptor` which uses kind for method dispatching.

Junit extensions postfix means: (c) - class context, (m) - method context

`ParameterResolver` not shown because it's called just before method execution.

`ExecutionCondition` and `TestExecutionExceptionHandler` are also out of usual lifecycle.


### Access storage from spock

Spock extension could access junit value storage with:

* `JunitExtensionSupport.getStore(SpecInfo, Namespace)` - obtain spec-level store
* `JunitExtensionSupport.getStore(IMethodInvocation, Namespace)` - obtain method or spec level store (depends on hook)

The second method is universal - always providing the most actual context (method, if available):

```groovy
IMethodInterceptor interceptor = { invocation ->
    Store store = JunitExtensionSupport.getStore(invocation, ExtensionContext.Namespace.create('name'))
}
```

The problem may only appear if you'll need to modify value stored on class level (in this case use
`JunitExtensionSupport.getStore(invocation.getSpec(), ExtensionContext.Namespace.create('name'))` to get class level storage
(common for all methods in test class)).

Complete usage example:

```groovy
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(StoreAwareExtension)
@interface StoreAware {
    String value() default "";
}

class StoreAwareExtension implements IAnnotationDrivenExtension<SpockStore> {
    @Override
    void visitSpecAnnotation(SpockStore annotation, SpecInfo spec) {
        ExtLifecycle ls = new ExtLifecycle()
        
        // listening for setup spec phase and test method execution
        spec.addSetupSpecInterceptor ls
        spec.allFeatures*.featureMethod*.addInterceptor ls
        
        // create store for extension values on class level
        Store store = JunitExtensionSupport.getStore(spec, ExtensionContext.Namespace.create(StoreAwareExtension.name))
        // and store annotation value there
        store.put('val', annotation.value())
    }
}

class ExtLifecycle extends AbstractMethodInterceptor {

    @Override
    void interceptSetupSpecMethod(IMethodInvocation invocation) throws Throwable {
        // access stored value
        Object value = JunitExtensionSupport.getStore(invocation, ExtensionContext.Namespace.create(StoreAwareExtension.name)).get('val')
        // do something
    }

    @Override
    void interceptFeatureMethod(final IMethodInvocation invocation) throws Throwable {
        // same store access here
    }
}
```

---
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)