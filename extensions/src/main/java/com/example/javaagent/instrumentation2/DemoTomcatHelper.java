/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation2;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.javaagent.bootstrap.servlet.AppServerBridge;
import io.opentelemetry.javaagent.instrumentation.servlet.ServletHelper;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.tomcat.util.buf.MessageBytes;

public class DemoTomcatHelper<REQUEST, RESPONSE> {
  protected final Instrumenter<Request, Response> instrumenter;
  protected final DemoTomcatServletEntityProvider<REQUEST, RESPONSE> servletEntityProvider;
  private final ServletHelper<REQUEST, RESPONSE> servletHelper;

  public DemoTomcatHelper(
      Instrumenter<Request, Response> instrumenter,
      DemoTomcatServletEntityProvider<REQUEST, RESPONSE> servletEntityProvider,
      ServletHelper<REQUEST, RESPONSE> servletHelper) {
    this.instrumenter = instrumenter;
    this.servletEntityProvider = servletEntityProvider;
    this.servletHelper = servletHelper;
  }

  public boolean shouldStart(Context parentContext, Request request) {
    return instrumenter.shouldStart(parentContext, request);
  }

  public Context start(Context parentContext, Request request) {
    Context context = instrumenter.start(parentContext, request);
    request.setAttribute(ServletHelper.CONTEXT_ATTRIBUTE, context);
    return context;
  }

  public void end(
      Request request, Response response, Throwable throwable, Context context, Scope scope) {
    if (scope == null) {
      return;
    }
    scope.close();

    if (throwable == null) {
      throwable = AppServerBridge.getException(context);
    }

    if (throwable != null || mustEndOnHandlerMethodExit(request)) {
      instrumenter.end(context, request, response, throwable);
    }
  }

  private boolean mustEndOnHandlerMethodExit(Request request) {
    REQUEST servletRequest = servletEntityProvider.getServletRequest(request);
    return servletRequest != null && servletHelper.mustEndOnHandlerMethodExit(servletRequest);
  }

  public void attachResponseToRequest(Request request, Response response) {
    REQUEST servletRequest = servletEntityProvider.getServletRequest(request);
    RESPONSE servletResponse = servletEntityProvider.getServletResponse(response);

    if (servletRequest != null && servletResponse != null) {
      servletHelper.setAsyncListenerResponse(servletRequest, servletResponse);
    }
  }

  static String messageBytesToString(MessageBytes messageBytes) {
    // on tomcat 10.1.0 MessageBytes.toString() has a side effect. Calling it caches the string
    // value and changes type of the MessageBytes from T_BYTES to T_STR which breaks request
    // processing in CoyoteAdapter.postParseRequest when it is called on MessageBytes from
    // request.requestURI().
    if (messageBytes.getType() == MessageBytes.T_BYTES) {
      return messageBytes.getByteChunk().toString();
    }
    return messageBytes.toString();
  }
}
