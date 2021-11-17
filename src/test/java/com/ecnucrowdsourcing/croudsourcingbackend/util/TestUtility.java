package com.ecnucrowdsourcing.croudsourcingbackend.util;

import org.json.JSONArray;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestUtility {

  public static final String DUMP_DIR = "D:/Datadump";

  public static final String SEP = "\t";

  public JSONArray readJSON(String filename) throws Exception {
    InputStream inputStream = TestUtility.class.getResourceAsStream(filename);
    int size=inputStream.available();
    byte[] buffer=new byte[size];
    inputStream.read(buffer);
    inputStream.close();
    String s = new String(buffer, StandardCharsets.UTF_8);
    return new JSONArray(s);
  }

  public BufferedReader readCSV(String filename) {
    InputStream inputStream = TestUtility.class.getResourceAsStream(filename);
    assert (inputStream != null);
    InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    return new BufferedReader(isr);
  }

  public <T> void dumpData(Class<T> clazz, ElasticsearchRepository<T, String> repo, Filter<T> filter) throws Exception {
    Field[] declaredFields = clazz.getDeclaredFields();
    String[] fields = new String[declaredFields.length];
    for (int i = 0; i < fields.length; i++) fields[i] = declaredFields[i].getName();
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, String.format("%s.csv", clazz.getSimpleName()))));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    bw.write(String.join(SEP, fields) + "\n");
    int pageIndex = 0;
    while (true) {
      Pageable pageable = PageRequest.of(pageIndex, 3000);
      System.out.println(pageable.getPageSize());
      Slice<T> itemPage = repo.findAll(pageable);
      for (T item : itemPage) {
        if (filter != null && !filter.filter(item)) continue;
        List<String> values = new ArrayList<>();
        for (Field field : declaredFields) {
          field.setAccessible(true);
          values.add(String.valueOf(field.get(item)));
        }
        bw.write(String.join(SEP, values) + "\n");
      }
      if (!itemPage.hasNext()) break;
      pageIndex++;
    }
    bw.close();
  }

  public static class Filter<T> {
    boolean filter(T t) {
      return true;
    }
  }
}
