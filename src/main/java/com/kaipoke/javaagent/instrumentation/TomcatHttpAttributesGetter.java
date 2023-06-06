package com.kaipoke.javaagent.instrumentation;

import static com.kaipoke.javaagent.instrumentation.TomcatHelper.messageBytesToString;

import io.opentelemetry.instrumentation.api.instrumenter.http.HttpServerAttributesGetter;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.tomcat.util.buf.MessageBytes;

public class TomcatHttpAttributesGetter implements HttpServerAttributesGetter<Request, Response> {

  @Override
  public String getMethod(Request request) {
    return messageBytesToString(request.method());
  }

  @Override
  @Nullable
  public String getTarget(Request request) {
    String target = messageBytesToString(request.requestURI());
    String queryString = messageBytesToString(request.queryString());
    if (queryString != null) {
      target += "?" + queryString;
    }
    return target;
  }

  @Override
  @Nullable
  public String getScheme(Request request) {
    MessageBytes schemeMessageBytes = request.scheme();
    return schemeMessageBytes.isNull() ? "http" : messageBytesToString(schemeMessageBytes);
  }

  @Override
  public List<String> getRequestHeader(Request request, String name) {
    return Collections.list(request.getMimeHeaders().values(name));
  }

  @Override
  @Nullable
  public Integer getStatusCode(Request request, Response response, @Nullable Throwable error) {
    return response.getStatus();
  }

  @Override
  public List<String> getResponseHeader(Request request, Response response, String name) {
    return Collections.list(response.getMimeHeaders().values(name));
  }
}
