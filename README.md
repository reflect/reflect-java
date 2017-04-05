# Reflect Java signed token generator

Usage:

```java
import io.reflect.token.Parameter;
import io.reflect.token.TokenGenerator;

Parameter[] params = new Parameter[]{
    new Parameter("Region", Parameter.Op.EQUALS, "Northwest")
}

String signedToken = TokenGenerator.generate("a1b2c3d4e5f6", params);
```
