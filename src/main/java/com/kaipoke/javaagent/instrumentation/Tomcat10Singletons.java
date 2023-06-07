package com.kaipoke.javaagent.instrumentation;

import org.apache.coyote.Request;
import org.apache.coyote.Response;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.javaagent.instrumentation.servlet.v5_0.Servlet5Accessor;
import io.opentelemetry.javaagent.instrumentation.servlet.v5_0.Servlet5Singletons;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class Tomcat10Singletons {
  private static final String INSTRUMENTATION_NAME =
      "com.kaipoke.xuan.opentelemetry.javaagent.extensions.instrumentation";

  private static final Instrumenter<Request, Response> INSTRUMENTER =
      TomcatInstrumenterFactory.create(INSTRUMENTATION_NAME, Servlet5Accessor.INSTANCE);

  private static final TomcatHelper<HttpServletRequest, HttpServletResponse> HELPER =
      new TomcatHelper<>(
          INSTRUMENTER, Tomcat10ServletEntityProvider.INSTANCE, Servlet5Singletons.helper());

  public static TomcatHelper<HttpServletRequest, HttpServletResponse> helper() {
    return HELPER;
  }

  private Tomcat10Singletons() {}
}
