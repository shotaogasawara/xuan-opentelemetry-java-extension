package com.kaipoke.xuan.opentelemetry.javaagent.extensions.instrumentation;

import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseMutator;
import org.apache.coyote.Response;

public enum DemoTomcat10ResponseMutator implements HttpServerResponseMutator<Response> {
  INSTANCE;

  DemoTomcat10ResponseMutator() {}

  @Override
  public void appendHeader(Response response, String name, String value) {
    response.addHeader(name, value);
  }
}
