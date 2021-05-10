package com.ecnucrowdsourcing.croudsourcingbackend.util;

import org.json.JSONArray;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestUtility {

  public static final String DUMP_DIR = "D:/Datadump";

  public JSONArray readJSON(String filename) throws Exception {
    InputStream inputStream = TestUtility.class.getResourceAsStream(filename);
    int size=inputStream.available();
    byte[] buffer=new byte[size];
    inputStream.read(buffer);
    inputStream.close();
    String s = new String(buffer, StandardCharsets.UTF_8);
    return new JSONArray(s);
  }

  public <T> void dumpData(Class<T> clazz, ElasticsearchRepository<T, String> repo) throws Exception {
    Field[] declaredFields = clazz.getDeclaredFields();
    String[] fields = new String[declaredFields.length];
    for (int i = 0; i < fields.length; i++) fields[i] = declaredFields[i].getName();
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, String.format("%s.csv", clazz.getSimpleName()))));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    bw.write(String.join(",", fields) + "\n");
    Iterable<T> items = repo.findAll();
    for (T item : items) {
      List<String> values = new ArrayList<>();
      for (Field field : declaredFields) {
        field.setAccessible(true);
        values.add(String.valueOf(field.get(item)));
      }
      bw.write(String.join(",", values) + "\n");
    }
    bw.close();
  }
}
