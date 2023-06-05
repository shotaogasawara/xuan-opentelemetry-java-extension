/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation2;

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
