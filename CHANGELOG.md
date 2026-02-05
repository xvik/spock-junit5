### 2.0.0 (2026-02-05)
* [Junit 6.0](https://docs.junit.org/6.0.2/release-notes.html) compatibility
    - Apply context API changes
    - Storage implementation completely updated (based on the new jupiter implementation to behave exactly the same)
* [BREAKING] Drop java 8-16 support (junit 6 dropped support for them)
* [BREAKING] Artifact name changed: **spock-junit6** 
  - version is not reset to 1.0 to preserve cross-versioning and to avoid usage mistakes
  - a different artifact name would avoid dependabot updates spamming for incompatible junit versions
* Fix spock 2.4 compatibility (spock 2.4 moved FEATURE_EXECUTION before initialization, which might cause problems 
  to feature interceptors, trying to access junit store). 

### 1.4.0 (2026-02-04)
* [Junit 5.12](https://docs.junit.org/5.12.1/release-notes/) compatibility
    - Implement missed extension context API methods
    - The new PreInterruptCallback callback is not supported (triggered by a not supported @Timeout extension)
* [Junit 5.13](https://docs.junit.org/5.13.4/release-notes/) compatibility
    - Implement missed extension context API methods
    - New callbacks BeforeClassTemplateInvocationCallback, AfterClassTemplateInvocationCallback 
      and ClassTemplateInvocationContextProvider not supported (no templates in spock)
* [Junit 5.14](https://docs.junit.org/5.14.2/release-notes.html) compatibility
    - Implement missed extension context API methods
* Add AutoCloseable values support for storage (CloseableResource is deprecated)

Note: there is no need for separate releases because only new methods were added

### 1.3.0 (2026-02-03)
* [Junit 5.11](https://docs.junit.org/5.11.0/release-notes/#release-notes-5.11.0-junit-jupiter-deprecations-and-breaking-changes) compatibility: 
    - @ExtendWith on a non-static field now participate in beforeAll/afterAll;
    - static extension fields are registered before non-static fields (junit behavior change fixing extensions order)
* Add support for LifecycleMethodExecutionExceptionHandler (for spock setupSpec, setup etc.)

### 1.2.0 (2022-11-25)
* Add root (engine-level) context to be able to use global storage in extensions (#44) 
  (context.getRoot().getStore())

### 1.1.0 (2022-09-08)
* Update to junit 5.9 (spock 2.2 compatibility)
   - added [ExecutableInvoker](https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/extension/ExecutableInvoker.html) support
     (might be obtained through extension context (getExecutableInvoker()) in order to manually call constructors and methods
      with registered [ParameterResolver extensions](https://junit.org/junit5/docs/current/user-guide/#extensions-parameter-resolution))

### 1.0.1 (2022-09-06)
* Add direct dependency to junit-jupiter-api to simplify usage and prevent running with older junit versions

### 1.0.0 (2022-02-15)
* Initial release