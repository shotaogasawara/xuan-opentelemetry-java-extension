package com.kaipoke.xuan.instrumentation;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static java.util.Collections.singletonList;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.List;
import net.bytebuddy.matcher.ElementMatcher;

@AutoService(InstrumentationModule.class)
public class Tomcat10InstrumentationModule extends InstrumentationModule {
  public Tomcat10InstrumentationModule() {
    super("xuan-tomcat", "xuan-tomcat-10.0");
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    // only matches tomcat 10.0+
    return hasClassesNamed("jakarta.servlet.ReadListener");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    String packageName = Tomcat10InstrumentationModule.class.getPackage().getName();
    return singletonList(
        new Tomcat10ServerHandlerInstrumentation(
            packageName + ".Tomcat10ServerHandlerAdvice",
            packageName + ".Tomcat10AttachResponseAdvice"));
  }

  @Override
  public boolean isHelperClass(String className) {
    return className.startsWith(
            "com.kaipoke.xuan.opentelemetry.javaagent.extensions.instrumentation")
        || className.startsWith("io.opentelemetry.javaagent.instrumentation")
        || className.startsWith("io.opentelemetry.javaagent.bootstrap")
        || className.startsWith("io.opentelemetry.instrumentation")
            && !className.startsWith("io.opentelemetry.instrumentation.api");
  }
}
