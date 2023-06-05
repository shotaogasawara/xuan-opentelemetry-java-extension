package com.kaipoke.xuan.opentelemetry.javaagent.extensions.instrumentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

public class DemoTomcat10ServletEntityProvider
    implements DemoTomcatServletEntityProvider<HttpServletRequest, HttpServletResponse> {
  public static final DemoTomcat10ServletEntityProvider INSTANCE =
      new DemoTomcat10ServletEntityProvider();

  private DemoTomcat10ServletEntityProvider() {}

  @Override
  public HttpServletRequest getServletRequest(Request request) {
    Object note = request.getNote(1);

    if (note instanceof HttpServletRequest) {
      return (HttpServletRequest) note;
    } else {
      return null;
    }
  }

  @Override
  public HttpServletResponse getServletResponse(Response response) {
    Object note = response.getNote(1);

    if (note instanceof HttpServletResponse) {
      return (HttpServletResponse) note;
    } else {
      return null;
    }
  }
}
