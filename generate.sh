#!/usr/bin/env sh

rm ./src/main/java/com/ecnucrowdsourcing/croudsourcingbackend/service/thrift/* -f
thrift -out ./src/main/java --gen java:beans thrift/ckqa.thrift
