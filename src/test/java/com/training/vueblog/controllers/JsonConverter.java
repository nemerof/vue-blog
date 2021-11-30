package com.training.vueblog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {

  public static String objectToJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
