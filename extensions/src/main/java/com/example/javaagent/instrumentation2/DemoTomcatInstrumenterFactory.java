/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation2;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpRouteHolder;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerMetrics;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanStatusExtractor;
import io.opentelemetry.javaagent.bootstrap.internal.CommonConfig;
import io.opentelemetry.javaagent.bootstrap.servlet.AppServerBridge;
import io.opentelemetry.javaagent.instrumentation.servlet.ServletAccessor;
import io.opentelemetry.javaagent.instrumentation.servlet.ServletErrorCauseExtractor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

public final class DemoTomcatInstrumenterFactory {

  private DemoTomcatInstrumenterFactory() {}

  public static <REQUEST, RESPONSE> Instrumenter<Request, Response> create(
      String instrumentationName, ServletAccessor<REQUEST, RESPONSE> accessor) {
    DemoTomcatHttpAttributesGetter httpAttributesGetter = new DemoTomcatHttpAttributesGetter();
    DemoTomcatNetAttributesGetter netAttributesGetter = new DemoTomcatNetAttributesGetter();

    return Instrumenter.<Request, Response>builder(
            GlobalOpenTelemetry.get(),
            instrumentationName,
            HttpSpanNameExtractor.create(httpAttributesGetter))
        .setSpanStatusExtractor(HttpSpanStatusExtractor.create(httpAttributesGetter))
        .setErrorCauseExtractor(new ServletErrorCauseExtractor<>(accessor))
        .addAttributesExtractor(
            HttpServerAttributesExtractor.builder(httpAttributesGetter, netAttributesGetter)
                .setCapturedRequestHeaders(CommonConfig.get().getServerRequestHeaders())
                .setCapturedResponseHeaders(CommonConfig.get().getServerResponseHeaders())
                .build())
        .addContextCustomizer(HttpRouteHolder.create(httpAttributesGetter))
        .addContextCustomizer(
            (context, request, attributes) ->
                new AppServerBridge.Builder()
                    .captureServletAttributes()
                    .recordException()
                    .init(context))
        .addOperationMetrics(HttpServerMetrics.get())
        .buildServerInstrumenter(DemoTomcatRequestGetter.INSTANCE);
  }
}
