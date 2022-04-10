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

@Service("ckqaService")
public class CKQAService {

  public List<Result> getMaskResult(String query) {
    List<Result> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("localhost", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.getMaskResult(query);
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
      transport = new TSocket("localhost", 8327);
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
      transport = new TSocket("localhost", 8327);
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
      transport = new TSocket("localhost", 8327);
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
      transport = new TSocket("localhost", 8327);
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
}
