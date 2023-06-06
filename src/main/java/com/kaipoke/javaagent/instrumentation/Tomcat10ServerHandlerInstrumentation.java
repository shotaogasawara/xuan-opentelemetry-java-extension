package com.kaipoke.javaagent.instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class Tomcat10ServerHandlerInstrumentation implements TypeInstrumentation {
  private final String handlerAdviceClassName;
  private final String attachResponseAdviceClassName;

  public Tomcat10ServerHandlerInstrumentation(
      String handlerAdviceClassName, String attachResponseAdviceClassName) {
    this.handlerAdviceClassName = handlerAdviceClassName;
    this.attachResponseAdviceClassName = attachResponseAdviceClassName;
  }

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("org.apache.catalina.connector.CoyoteAdapter");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("service"))
            .and(takesArgument(0, named("org.apache.coyote.Request")))
            .and(takesArgument(1, named("org.apache.coyote.Response"))),
        handlerAdviceClassName);

    transformer.applyAdviceToMethod(
        isMethod()
            .and(named("postParseRequest"))
            .and(takesArgument(0, named("org.apache.coyote.Request")))
            .and(takesArgument(2, named("org.apache.coyote.Response")))
            .and(returns(boolean.class)),
        attachResponseAdviceClassName);
  }
}
