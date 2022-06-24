package com.ecnucrowdsourcing.croudsourcingbackend.service;

import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Result;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.CKQA;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Tuple;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service("ckqaService")
public class CKQAService {

  public List<Result> getMaskResult(String query, Boolean includeNone, Boolean includeCSKG) {
    List<Result> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("192.168.10.67", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.getMaskResult(query, includeNone, includeCSKG);
      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    }

    return results;
  }

  public Map<String, String> get_cms(String query, int video) {
    Map<String, String> results = new HashMap<String, String>();
    // List<CMS> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("192.168.10.67", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.get_cms(query, video);
      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    }

    return results;
  }

  public List<Result> getSpanResult(String query) {
    List<Result> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("192.168.10.67", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.getSpanResult(query);
      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    }

    return results;
  }

  public List<Result> getMaskWordResult(String query) {
    List<Result> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("192.168.10.67", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.getMaskWordResult(query);
      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    }

    return results;
  }

  public List<Tuple> getExtraction(String query) {
    List<Tuple> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("192.168.10.67", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.getExtraction(query);
      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    }

    return results;
  }

  public List<Result> getTextQaResult(String query, String text) {
    List<Result> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("192.168.10.67", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.getTextQaResult(query, text);
      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    }

    return results;
  }

  public List<Double> getEntailment(String premise, List<String> hypothesises) {
    List<Double> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("localhost", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.getEntailment(premise, hypothesises);
      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    }

    return results;
  }
}
