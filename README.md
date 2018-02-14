# Reflect Java signed token generator

Usage:

```java
import io.reflect.token.Parameter;
import io.reflect.token.TokenBuilder;

String accessKey = "d232c1e5-6083-4aa7-9042-0547052cc5dd";
String secretKey = "74678a9b-685c-4c14-ac45-7312fe29de06";

Parameter parameter = new Parameter("user-id", Parameter.Op.EQUALS, "1234");

String token = new TokenBuilder(accessKey)
        .setAttribute("user-id", 1234)
        .setAttribute("user-name", "Billy Bob")
        .addParameter(parameter)
        .build(secretKey);
```
