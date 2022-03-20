namespace java com.ecnucrowdsourcing.croudsourcingbackend.service.thrift

struct Result {
    1: required string source;
    2: required list<string> answers;
    3: required string context;
}

struct Tuple {
    1: required list<string> values;
    2: required double score;
}

service CKQA
{
    list<Result> getMaskResult(1:string query);
    list<Result> getSpanResult(1:string query);
    list<Result> getMaskWordResult(1:string query);

    list<Tuple> getExtraction(1:string query);
}
