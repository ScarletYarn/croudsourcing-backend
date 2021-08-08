namespace java com.ecnucrowdsourcing.croudsourcingbackend.service.thrift

struct Result {
    1: required string source;
    2: required list<string> answers;
    3: required string context;
}

service CKQA
{
    list<Result> getResult(1:string query)
}
