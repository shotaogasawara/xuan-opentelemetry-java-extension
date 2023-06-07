package com.kaipoke.javaagent.instrumentation;

import java.util.List;

import javax.annotation.Nullable;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpClientAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpCommonAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerAttributesGetter;

public final class XuanHttpSpanStatusExtractor<REQUEST, RESPONSE>
    implements SpanStatusExtractor<REQUEST, RESPONSE> {

  /**
   * Returns the {@link SpanStatusExtractor} for HTTP requests, which will use the
   * HTTP status code
   * to determine the {@link StatusCode} if available or fallback to
   * {@linkplain #getDefault() the
   * default status} otherwise.
   */
  public static <REQUEST, RESPONSE> SpanStatusExtractor<REQUEST, RESPONSE> create(
      HttpClientAttributesGetter<? super REQUEST, ? super RESPONSE> getter) {
    return new XuanHttpSpanStatusExtractor<>(getter, XuanHttpStatusConverter.CLIENT);
  }

  /**
   * Returns the {@link SpanStatusExtractor} for HTTP requests, which will use the
   * HTTP status code
   * to determine the {@link StatusCode} if available or fallback to
   * {@linkplain #getDefault() the
   * default status} otherwise.
   */
  public static <REQUEST, RESPONSE> SpanStatusExtractor<REQUEST, RESPONSE> create(
      HttpServerAttributesGetter<? super REQUEST, ? super RESPONSE> getter) {
    return new XuanHttpSpanStatusExtractor<>(getter, XuanHttpStatusConverter.SERVER);
  }

  private final HttpCommonAttributesGetter<? super REQUEST, ? super RESPONSE> getter;
  private final XuanHttpStatusConverter statusConverter;

  private XuanHttpSpanStatusExtractor(
      HttpCommonAttributesGetter<? super REQUEST, ? super RESPONSE> getter,
      XuanHttpStatusConverter statusConverter) {
    this.getter = getter;
    this.statusConverter = statusConverter;
  }

  @Override
  public void extract(
      SpanStatusBuilder spanStatusBuilder,
      REQUEST request,
      @Nullable RESPONSE response,
      @Nullable Throwable error) {

    if (response != null) {
      Integer statusCode = getter.getStatusCode(request, response, error);
      if (statusCode != null) {
        // ヘッダにgraphqlエラーが検出された場合はstatusをエラーに変更する
        List<String> errorTypes = getter.getResponseHeader(request, response, "x-graphql-error-type");
        if(!errorTypes.isEmpty()){
          spanStatusBuilder.setStatus(StatusCode.ERROR);
          System.out.println("Set status code to error.");
          return;
        }

        // それ以外の場合はhttpステータスコードに基づいて判定
        StatusCode statusCodeObj = statusConverter.statusFromHttpStatus(statusCode);
        if (statusCodeObj == StatusCode.ERROR) {
          spanStatusBuilder.setStatus(statusCodeObj);
          return;
        }
      }
    }
    SpanStatusExtractor.getDefault().extract(spanStatusBuilder, request, response, error);
  }
}
