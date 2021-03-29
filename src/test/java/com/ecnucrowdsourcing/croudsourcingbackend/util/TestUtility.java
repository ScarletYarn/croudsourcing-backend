package com.ecnucrowdsourcing.croudsourcingbackend.util;

import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class TestUtility {

  public JSONArray readJSON(String filename) throws Exception {
    InputStream inputStream = TestUtility.class.getResourceAsStream(filename);
    int size=inputStream.available();
    byte[] buffer=new byte[size];
    inputStream.read(buffer);
    inputStream.close();
    String s = new String(buffer, StandardCharsets.UTF_8);
    return new JSONArray(s);
  }
}
