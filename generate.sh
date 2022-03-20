#!/usr/bin/env sh

if [ -d "./src/main/java/com/ecnucrowdsourcing/croudsourcingbackend/service/thrift" ]
then
  rm -rf ./src/main/java/com/ecnucrowdsourcing/croudsourcingbackend/service/thrift/*
fi

thrift -out ./src/main/java --gen java:beans thrift/ckqa.thrift
