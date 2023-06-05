/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation2;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.javaagent.instrumentation.servlet.v5_0.Servlet5Accessor;
import io.opentelemetry.javaagent.instrumentation.servlet.v5_0.Servlet5Singletons;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

public final class DemoTomcat10Singletons {
  private static final String INSTRUMENTATION_NAME = "com.example.javaagent.instrumentation2";
  private static final Instrumenter<Request, Response> INSTRUMENTER =
      DemoTomcatInstrumenterFactory.create(INSTRUMENTATION_NAME, Servlet5Accessor.INSTANCE);
  private static final DemoTomcatHelper<HttpServletRequest, HttpServletResponse> HELPER =
      new DemoTomcatHelper<>(
          INSTRUMENTER, DemoTomcat10ServletEntityProvider.INSTANCE, Servlet5Singletons.helper());

  public static DemoTomcatHelper<HttpServletRequest, HttpServletResponse> helper() {
    return HELPER;
  }

  private DemoTomcat10Singletons() {}
}
