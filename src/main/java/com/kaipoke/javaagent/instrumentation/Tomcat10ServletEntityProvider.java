package com.kaipoke.javaagent.instrumentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

public class Tomcat10ServletEntityProvider
    implements TomcatServletEntityProvider<HttpServletRequest, HttpServletResponse> {
  public static final Tomcat10ServletEntityProvider INSTANCE = new Tomcat10ServletEntityProvider();

  private Tomcat10ServletEntityProvider() {}

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
