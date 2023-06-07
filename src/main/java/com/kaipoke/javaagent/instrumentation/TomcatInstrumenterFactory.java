package com.kaipoke.javaagent.instrumentation;

import org.apache.coyote.Request;
import org.apache.coyote.Response;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpRouteHolder;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerMetrics;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanNameExtractor;
import io.opentelemetry.javaagent.bootstrap.internal.CommonConfig;
import io.opentelemetry.javaagent.bootstrap.servlet.AppServerBridge;
import io.opentelemetry.javaagent.instrumentation.servlet.ServletAccessor;
import io.opentelemetry.javaagent.instrumentation.servlet.ServletErrorCauseExtractor;

public final class TomcatInstrumenterFactory {

  private TomcatInstrumenterFactory() {}

  public static <REQUEST, RESPONSE> Instrumenter<Request, Response> create(
      String instrumentationName, ServletAccessor<REQUEST, RESPONSE> accessor) {
    TomcatHttpAttributesGetter httpAttributesGetter = new TomcatHttpAttributesGetter();
    TomcatNetAttributesGetter netAttributesGetter = new TomcatNetAttributesGetter();

    return Instrumenter.<Request, Response>builder(
            GlobalOpenTelemetry.get(),
            instrumentationName,
            HttpSpanNameExtractor.create(httpAttributesGetter))
        .setSpanStatusExtractor(XuanHttpSpanStatusExtractor.create(httpAttributesGetter))
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
        .buildServerInstrumenter(TomcatRequestGetter.INSTANCE);
  }
}
