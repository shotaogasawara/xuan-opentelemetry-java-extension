/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation2;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static java.util.Collections.singletonList;

import java.util.List;

import com.google.auto.service.AutoService;

import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

@AutoService(InstrumentationModule.class)
public class DemoTomcat10InstrumentationModule extends InstrumentationModule {
  public DemoTomcat10InstrumentationModule() {
    super("demo2-tomcat", "demo2-tomcat-10.0");
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    // only matches tomcat 10.0+
    return hasClassesNamed("jakarta.servlet.ReadListener");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    String packageName = DemoTomcat10InstrumentationModule.class.getPackage().getName();
    return singletonList(
        new DemoTomcat10ServerHandlerInstrumentation(
            packageName + ".DemoTomcat10ServerHandlerAdvice",
            packageName + ".DemoTomcat10AttachResponseAdvice"));
  }

  @Override
  public boolean isHelperClass(String className) {
    return className.startsWith("com.example.javaagent.instrumentation2")
        || className.startsWith("io.opentelemetry.javaagent.instrumentation")
        || className.startsWith("io.opentelemetry.javaagent.bootstrap")
        || className.startsWith("io.opentelemetry.instrumentation")
        && !className.startsWith("io.opentelemetry.instrumentation.api");
  }

  // どうもこちらで
  // @Override
  // public List<String> getAdditionalHelperClassNames() {
  //   return Arrays.asList(
  //     DemoTomcat10Singletons.class.getName(),
  //     DemoTomcat10ServerHandlerAdvice.class.getName()

  //     );
  // }
}
