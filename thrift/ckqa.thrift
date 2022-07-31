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

struct Scale {
	1: required i32 entityCount;
	2: required i32 entityCountCn;
}

struct CompletionResult {
	1: required string item;
	2: required double score;
	3: required bool exist;
}

service CKQA
{
    list<Result> getMaskResult(1:string query, 2:bool includeNone, 3:bool includeCSKG);
    list<Result> getSpanResult(1:string query);
    list<Result> getMaskWordResult(1:string query);
    list<Result> getTextQaResult(1:string query, 2:string text);

    list<Tuple> getExtraction(1:string query);
    list<double> getEntailment(1:string premise, 2:list<string> hypothesises);

    map<string,string> get_cms(1:string query,2:i32 video);

    Scale getScale();

    list<CompletionResult> getCompletion(1:string head, 2:string rel, 3:bool isInv);
    void upsert(1:string id, 2: string subject, 3:string relation, 4:string object);
}
