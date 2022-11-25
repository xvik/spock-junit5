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