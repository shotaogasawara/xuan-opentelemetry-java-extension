package com.kaipoke.javaagent.instrumentation;

import org.apache.coyote.Response;

import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseMutator;

public enum Tomcat10ResponseMutator implements HttpServerResponseMutator<Response> {
  INSTANCE;

  Tomcat10ResponseMutator() {}

  @Override
  public void appendHeader(Response response, String name, String value) {
    response.addHeader(name, value);
  }
}
