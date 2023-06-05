package com.kaipoke.xuan.instrumentation;

import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseMutator;
import org.apache.coyote.Response;

public enum Tomcat10ResponseMutator implements HttpServerResponseMutator<Response> {
  INSTANCE;

  Tomcat10ResponseMutator() {}

  @Override
  public void appendHeader(Response response, String name, String value) {
    response.addHeader(name, value);
  }
}
