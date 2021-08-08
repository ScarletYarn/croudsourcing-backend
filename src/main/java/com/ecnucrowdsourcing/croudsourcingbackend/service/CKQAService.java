package com.ecnucrowdsourcing.croudsourcingbackend.service;

import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Result;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.CKQA;
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

  public List<Result> getResult(String query) {
    List<Result> results = new ArrayList<>();
    try {
      TTransport transport;
      transport = new TSocket("192.168.10.162", 8327);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      CKQA.Client client = new CKQA.Client(protocol);
      results = client.getResult(query);
      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    }

    return results;
  }
}
