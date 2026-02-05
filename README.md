# spock-junit5

[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![CI](https://github.com/xvik/spock-junit5/actions/workflows/CI.yml/badge.svg)](https://github.com/xvik/spock-junit5/actions/workflows/CI.yml)
[![Appveyor build status](https://ci.appveyor.com/api/projects/status/github/xvik/spock-junit5?svg=true)](https://ci.appveyor.com/project/xvik/spock-junit5)
[![codecov](https://codecov.io/gh/xvik/spock-junit5/branch/master/graph/badge.svg)](https://codecov.io/gh/xvik/spock-junit5)

**Pay attention**: artifacts differ for junit 5 and junit 6 (see [compatibility](#compatibility) section):

* `spock-junit6` for junit 6
* `spock-junit5` for junit 5

Version is not reset for spock-junit6 to avoid usage mistakes.   
Also, different artifact names should avoid dependabot "spam" for older versions

`spock-junit5` is in the [separate branch](https://github.com/xvik/spock-junit5/tree/junit5)

### About

Junit 5 and 6 (jupiter) [extensions](https://junit.org/junit5/docs/current/user-guide/#extensions) support
for [Spock Framework](https://spockframework.org/) 2: allows using junit 5 (6) extension in spock (like it was with
junit 4 rules in spock 1).

Features:

* Supports almost all junit extension types. Problem may appear only with extensions requiring `TestInstanceFactory`
  which is impossible to support because spock manage test instance itself.
    - Warning message would indicate not supported extension types (if usage detected)
* Supports all the same registration types:
    - @ExtendWith on class, method, field or param
    - Custom annotations on class, method, field or param
    - @RegisterExtension on static and usual fields (direct registration)
* Supports parameters injection in test and fixture methods (but not in constructor)
* Support junit `ExecutionConditions` (for example, junit `@Disabled` would work)
* Implicit activation: implemented as global spock extension
* API for spock extensions to use junit value storage
  (to access values stored by junit extensions or for direct usage because spock does not provide such feature)

Extensions behaviour is the same as in jupiter engine (the same code used where possible). Behavior in both engines
validated with tests.

#### Motivation

Originally developed for [dropwizard-guicey](https://github.com/xvik/dropwizard-guicey) to avoid maintaining special
spock extensions.

Spock 1 provides spock-junit4 module to support junit 4 rules. At that time spock extensions model was "light years"
ahead
of junit. But junit 5 extensions are nearly equal in power to spock extensions (both has pros and cons). It is a big
loss for spock to ignore junit5 extensions:
junit extensions are easier to write, but spock is still much better for writing tests.

Module named `spock-junit5` by analogy with legacy `spock-junit4` module.
There should be no official `spock-junit5` module so name would stay unique
(there are discussions about official `spock-jupiter` module with value storage implementation).

[More details about motivation and realization in the blog post](https://blog.vyarus.ru/using-junit-5-extensions-in-spock-2-tests).

### Setup

[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/spock-junit5.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/spock-junit5)

Maven:

```xml

<dependency>
    <groupId>ru.vyarus</groupId>
    <artifactId>spock-junit6</artifactId>
    <version>2.0.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'ru.vyarus:spock-junit6:2.0.0'
```

**For junit 5 setup** see [junit5 docs](https://github.com/xvik/spock-junit5/tree/junit5).

### Compatibility

`spock-junit6` compiled for java 17 (junit6 requires java 17), junit 6  
`spock-junit5` compiled for java 8 (tested up to java 21), junit 5.14

The only transitive library dependency is *junit-jupiter-api*: to bring in required junit annotations
and to prevent usage with lower junit versions.

There is a high chance that a more recent module version will work with older junit versions
(it depends on what extensions are used).
In case of problems (like `NoClassDefFoundError`) select a lower module version (according to your junit version)

| Junit       | Artifact                | Version                                                  | Junit API Changes                                            
|-------------|-------------------------|----------------------------------------------------------|--------------------------------------------------------------
| 6.0         | **spock&#x2011;junit6** | [2.0.0](https://github.com/xvik/spock-junit5/tree/2.0.0) | value storage API changes, java 17 required                              
| 5.12 - 5.14 | spock&#x2011;junit5     | [1.4.0](https://github.com/xvik/spock-junit5/tree/1.4.0) | new methods in ExtensionContext                              
| 5.11        | spock&#x2011;junit5     | [1.3.0](https://github.com/xvik/spock-junit5/tree/1.3.0) | changed initialization order for non-static extension fields 
| 5.9 - 5.10  | spock&#x2011;junit5     | [1.2.0](https://github.com/xvik/spock-junit5/tree/1.2.0) |
| 5.7 - 5.8   | spock&#x2011;junit5     | [1.0.1](https://github.com/xvik/spock-junit5/tree/1.0.1) |

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

NOTE: `@Shared` spock fields are not supported

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
* LifecycleMethodExecutionExceptionHandler

Not supported:

* TestTemplateInvocationContextProvider - junit specific feature (no need for support)
* ClassTemplateInvocationContextProvider
* BeforeClassTemplateInvocationCallback
* AfterClassTemplateInvocationCallback
* TestInstanceFactory - impossible to support because spock does not delegate test instance creation
* TestInstancePreConstructCallback - impossible to support because spock does not delegate test instance creation
* InvocationInterceptor - could be supported, but it is very specific
* PreInterruptCallback - triggered by @Timeout extensions which is not supported (requires InvocationInterceptor)
* TestWatcher - no need in context of spock

Of course, constructor parameters injection is not supported because spock does not allow spec constructors.

**What does "extension not supported" mean?**  
If extension implements not supported extension interface, like `TestWatcher`,
then methods for this interface would not be called on extension. That's all.  
Such interfaces might be used in extension for some "additional features" like logging and
so extension would work perfectly in spock.

##### Default junit extensions

Junit register some extensions by default:

```java
    private static final List<Extension> DEFAULT_STATELESS_EXTENSIONS = Collections.unmodifiableList(Arrays.asList(//
        new DisabledCondition(),
        new AutoCloseExtension(),
        new TimeoutExtension(),
        new RepeatedTestExtension(),
        new TestInfoParameterResolver(),
        new TestReporterParameterResolver()));
```

Plus, junit loads extensions from `META-INF/services/org.junit.jupiter.api.extension.Extension` file.

**None of this would work in context of spock**.   
This is intentional: as not all extensions are supported, then automatic loading may
cause unexpected behavior. If you need any default extension - register it manually.

Annotation-based extensions like `@Disabled` or `@AutoClose` would work in spock because they are declared with
annotations.
Extensions like `TestInfoParameterResolver` could be easully enabled with `@ExtendWith(TestInfoParameterResolver.class)`

So overall not automatic defaults should not be a problem.

### Usage with Spring-Boot

Only spock and spock-junit5 dependencies would be required:

**Spring-boot 4**:

```groovy
testImplementation 'org.spockframework:spock-core:2.4-groovy-5.0'
testImplementation 'ru.vyarus:spock-junit6:2.0.0'
```

**Spring-boot 3**:

```groovy
testImplementation 'org.spockframework:spock-core:2.4-groovy-4.0'
testImplementation 'ru.vyarus:spock-junit5:1.4.0'
```

Note that [spock-spring module](https://spockframework.org/spock/docs/2.3/modules.html#_spring_module)
**should not be used** together with spock-junit5 for spring-boot tests!  
Spock-junit5 would activate native spring junit5 extensions **the same way** as in raw junit

Example MVC test (based
on [this example](https://github.com/mkyong/spring-boot/blob/master/spring-boot-hello-world/src/test/java/com/mkyong/HelloControllerTests.java)):

```groovy
@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    def "Test welcome ok"() {

        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Hello World, Spring Boot!")))

        expect:
        // ofc. it's a bad spock test, but just to show that extensions work the same way
        true
    }
}
```

Example JPA test (based
on [this example](https://github.com/mkyong/spring-boot/blob/master/spring-data-jpa/src/test/java/com/mkyong/BookRepositoryTest.java)):

```groovy
@DataJpaTest
class BootTest extends Specification {

    @Autowired
    TestEntityManager testEM
    @Autowired
    BookRepository bookRepository

    void setup() {
        bookRepository.deleteAll()
        bookRepository.flush()
        testEM.clear()
    }

    def "Test save"() {

        when:
        Book b1 = new Book("Book A", BigDecimal.valueOf(9.99), LocalDate.of(2023, 8, 31))
        bookRepository.save(b1)
        Long savedBookID = b1.getId()
        Book book = bookRepository.findById(savedBookID).orElseThrow()

        then:
        savedBookID == book.getId()
        "Book A" == book.getTitle()
        BigDecimal.valueOf(9.99) == book.getPrice()
        LocalDate.of(2023, 8, 31) == book.getPublishDate()
    }
}
```

### Spock @Shared state

**Junit extensions would not be able to initialize `@Shared` fields**. So just don't use `@Shared`
on fields that must be initialized by junit extensions.

The reason is: shared fields are managed on a special test instance, different
from instance used for test execution. But in junit lifecycle beforeEach
could be called only once (otherwise extensions may work incorrectly)
and so it is called only with actual test instance.

Even if junit extension initialize `@Shared` field - it would make no effect because
it would be done on test instance instead of shared test instance and
on field access spock will return shared instance field value (not initialized - most likely, null).

This limitation should not be a problem.

### Lifecycle

Junit extensions support is implemented as global spock extension and so it would be executed before any other
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
@ExtensionAnnotation(SpockExtensionImpl)
@interface SpockExtension {
    String value() default "";
}

class SpockExtensionImpl implements IAnnotationDrivenExtension<SpockExtension> {
    @Override
    void visitSpecAnnotation(SpockExtension annotation, SpecInfo spec) {
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

    static class I implements IMethodInterceptor {
        ...
    }
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
|                                    | feature                    | FEATURE_EXECUTION*  | spec.allFeatures*.addInterceptor                |
|                                    | iteration                  | ITERATION_EXECUTION | spec.allFeatures*.addIterationInterceptor       |
| BeforeEachCallback (m)             |                            |                     |                                                 |
|                                    | setup                      | SETUP               | spec.addSetupInterceptor                        |
|                                    | setup method               | SETUP               | spec.setupMethods*.addInterceptor               |
|                                    | fixture method             | SETUP               | spec.allFixtureMethods*.addInterceptor          | 
|                                    | **TEST setup**             |                     |                                                 |
| BeforeTestExecutionCallback (m)    |                            |                     |                                                 |
|                                    | feature method             | FEATURE             | spec.allFeatures*.featureMethod*.addInterceptor |
|                                    | **TEST body**              |                     |                                                 |
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

\* in spock 2.4 `FEATURE_EXECUTION` goes before `INITIALIZER`

Kind is a `IMethodInvocation.getMethod().getKind()`. It is shown in case if you use `AbstractMethodInterceptor` which
uses kind for method dispatching.

Junit extensions postfix means: (c) - class context, (m) - method context

`ParameterResolver` not shown because it's called just before method execution.

`ExecutionCondition` and `TestExecutionExceptionHandler` are also out of usual lifecycle.

#### Contexts hierarchy

Library use simplified junit contexts hierarchy:

1. Global (engine) context
2. Class context (created for each test class)
3. Method context (created for each test method or data iteration)

In junit there are other possible contexts (for some specific features), but they are nto useful in context of spock.

Global context created once for all tests. It might be used as a global data store:

```java
// BeforeAllCallback
public void beforeAll(ExtensionContext context) throws Exception {
    // global storage (same for all tests)
    Store store = context.getRoot().getStore(Namespace.create("ext"));
    if (store.get("some") == null) {
        // would be called only in first test with this extension 
        // (for other tests value would be preserved) 
        store.put("some", "val");
    }
}
```

NOTE: this works exactly the same as it works in junit jupiter (showed just to confirm this ability).

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
`JunitExtensionSupport.getStore(invocation.getSpec(), ExtensionContext.Namespace.create('name'))` to get class level
storage
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
