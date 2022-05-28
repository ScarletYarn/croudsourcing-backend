namespace java com.ecnucrowdsourcing.croudsourcingbackend.service.thrift

struct Result {
    1: required string source;
    2: required list<string> answers;
    3: required string context;
}

struct Tuple {
    1: required list<string> values;
    2: required double score;
    3: required list<double> vector;
}

struct CMS {
    1: required string cmsName;
    2: required string cmsValue;
}

service CKQA
{
    list<Result> getMaskResult(1:string query, 2:bool includeNone, 3:bool includeCSKG);
    list<Result> getSpanResult(1:string query);
    list<Result> getMaskWordResult(1:string query);
    list<Result> getTextQaResult(1:string query, 2:string text);

    list<Tuple> getExtraction(1:string query);

    # list<CMS> get_cms(1:string query, 2:i32 video);
    map<string,string> get_cms(1:string query, 2:i32 video);
}
