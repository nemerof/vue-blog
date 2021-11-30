package com.training.vueblog.controllers;

import static com.training.vueblog.data.UserTestData.USER;

import com.training.vueblog.objects.User;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PrincipalDetailsArgumentResolver implements HandlerMethodArgumentResolver {
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().isAssignableFrom(User.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    return USER;
  }
}
