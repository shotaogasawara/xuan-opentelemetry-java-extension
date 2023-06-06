package com.kaipoke.javaagent.instrumentation;

import java.util.Collections;

import org.apache.coyote.Request;

import io.opentelemetry.context.propagation.TextMapGetter;

enum TomcatRequestGetter implements TextMapGetter<Request> {
  INSTANCE;

  @Override
  public Iterable<String> keys(Request carrier) {
    return Collections.list(carrier.getMimeHeaders().names());
  }

  @Override
  public String get(Request carrier, String key) {
    return carrier.getHeader(key);
  }
}
