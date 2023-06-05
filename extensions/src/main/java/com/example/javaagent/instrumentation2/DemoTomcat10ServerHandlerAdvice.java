/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation2;

import static com.example.javaagent.instrumentation2.DemoTomcat10Singletons.helper;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseCustomizerHolder;
import net.bytebuddy.asm.Advice;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

@SuppressWarnings("unused")
public class DemoTomcat10ServerHandlerAdvice {

  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static void onEnter(
      @Advice.Argument(0) Request request,
      @Advice.Argument(1) Response response,
      @Advice.Local("otelContext") Context context,
      @Advice.Local("otelScope") Scope scope) {

    try {
      Context parentContext = Java8BytecodeBridge.currentContext();
      if (!helper().shouldStart(parentContext, request)) {
        return;
      }
      context = helper().start(parentContext, request);
      scope = context.makeCurrent();
      System.out.println("call mutator");
      HttpServerResponseCustomizerHolder.getCustomizer()
          .customize(context, response, DemoTomcat10ResponseMutator.INSTANCE);
    } catch (Throwable e) {
      System.out.println("Some error happened!");
      System.out.println(e.getClass());
      System.out.println(e.getMessage());
      throw e;
    }
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
  public static void stopSpan(
      @Advice.Argument(0) Request request,
      @Advice.Argument(1) Response response,
      @Advice.Thrown Throwable throwable,
      @Advice.Local("otelContext") Context context,
      @Advice.Local("otelScope") Scope scope) {

    helper().end(request, response, throwable, context, scope);
  }
}
