- 人机回路众包设计平台后端

华东师范大学人机回路众包设计平台后端

# Trivial

- Kill dangling process on windows

```bat
netstat -ano | findstr "8080"
taskkill /pid 20984 /f
```

- Insert documents

```shell script
curl -XDELETE "192.168.10.162:9200/demo_dev_job"
```

- Delete documents

```shell script
curl -H "Content-Type: application/json" -XPOST "192.168.10.162:9200/demo_dev_rule_data/_bulk?pretty&refresh" --data-binary "@rule_data.json"
```

- Read resource file in Java

```java
InputStream inputStream = CroudsourcingbackendApplicationTests.class.getResourceAsStream("/rule_data.json");
int size=inputStream.available();
byte[] buffer=new byte[size];
inputStream.read(buffer);
inputStream.close();
String s = new String(buffer, StandardCharsets.UTF_8);
```

- Enum in Java

```java
Answer.valueOf("TRUE"); // Yield Answer.TRUE
Answer.TRUE.name(); // Yield "TRUE"
```

- Download using cURL

Some [resources](https://www.pair.com/support/kb/paircloud-downloading-files-with-curl/#setting-the-output-file)

```bash
curl -o <filename> <url>
```

# Bugs

- Documents inserted using cURL cannot be retrieved 

```shell script
curl -H "Content-Type: application/json" -XPOST "192.168.10.162:9200/demo_dev_rule_data/_bulk?pretty&refresh" --data-binary "@rule_data.json"
```

File rule_data.json(no new line within an object, otherwise it won't be recognized):

```json
{"index": {"_index": "demo_dev_rule_data", "_id": "8o3hf98qZ938hF9fh"}}
{"_class":"com.ecnucrowdsourcing.croudsourcingbackend.entity.RuleData", "jodId": "4oMWgncBWKp78TZvIVfH", "content": "国籍(A,v0) & 简称(v0,v1) & 出生地(B,v0) =>配偶(A,B)", "graph": "/img/kgdemo.png", "nl": "v0和A结婚，B的母亲是v0，那么得出B的父亲是A。", "instance": "结婚(梅拉尼娅·特朗普,唐纳德·特朗普) & 母亲(伊万卡·特朗普,梅拉尼娅·特朗普 ) =>父亲(伊万卡·特朗普,唐纳德·特朗普 )", "goldenAnswer": "0"}
{"index": {"_index": "demo_dev_rule_data", "_id": "8o3hf4oMWgncBWKph"}}
{"_class":"com.ecnucrowdsourcing.croudsourcingbackend.entity.RuleData", "jodId": "4oMWgncBWKp78TZvIVfH", "content": "国籍(A,v0) & 简称(v0,v1) & 出生地(B,v0) =>配偶(A,B)", "graph": "/img/kgdemo.png", "nl": "v0和A结婚，B的母亲是v0，那么得出B的父亲是A。", "instance": "结婚(梅拉尼娅·特朗普,唐纳德·特朗普) & 母亲(伊万卡·特朗普,梅拉尼娅·特朗普 ) =>父亲(伊万卡·特朗普,唐纳德·特朗普 )", "goldenAnswer": "0"}
```

Query for jobId=4oMWgncBWKp78TZvIVfH, got nothing

