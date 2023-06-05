/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation2;

import static com.example.javaagent.instrumentation2.DemoTomcat10Singletons.helper;

import net.bytebuddy.asm.Advice;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

@SuppressWarnings("unused")
public class DemoTomcat10AttachResponseAdvice {

  @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
  public static void attachResponse(
      @Advice.Argument(0) Request request,
      @Advice.Argument(2) Response response,
      @Advice.Return boolean success) {

    if (success) {
      helper().attachResponseToRequest(request, response);
    }
  }
}
