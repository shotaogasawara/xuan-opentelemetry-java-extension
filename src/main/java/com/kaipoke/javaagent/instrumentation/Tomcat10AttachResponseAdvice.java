package com.kaipoke.javaagent.instrumentation;

import static com.kaipoke.javaagent.instrumentation.Tomcat10Singletons.helper;

import org.apache.coyote.Request;
import org.apache.coyote.Response;

import net.bytebuddy.asm.Advice;

@SuppressWarnings("unused")
public class Tomcat10AttachResponseAdvice {

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
