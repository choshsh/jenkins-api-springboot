package com.choshsh.jenkinsapispringboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class HttpLogInterceptor implements HandlerInterceptor {

  private long timeReq;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    timeReq = System.currentTimeMillis();
    return true;
  }

  /**
   * http 통신 로그. latency 가시화 및 json 출력을 목적으로 했는데 다른 방법도 있는지 확인 필요
   *
   * @param request
   * @param response
   * @param handler
   * @param ex
   */
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    log.info("{ method: \"{}\", statusCode: \"{}\", user: \"{}\", latency: \"{}ms\", uri: \"{}\" }"
        , request.getMethod(), response.getStatus(), request.getHeader("Authorization")
        , System.currentTimeMillis() - timeReq, request.getRequestURI());
  }
}
