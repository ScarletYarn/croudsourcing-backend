#人机回路众包设计平台后端

华东师范大学人机回路众包设计平台后端


## Project Setup

- Start up
```shell
./gradlew run
```

- Build
```shell
./gradlew bootJar
```

- Connect to server

Change IP address `java/com/ecnucrowdsourcing/croudsourcingbackend/config/RestClientConfig.java:21` to 192.168.10.162

## Get Started
- Env

Language: Java 8~15

RPC structure: thrift 


- Windows

Generate ckqa.thrift

```bat
generate.bat
```

Run server

```bat
gradlew.bat run
(if failed)
gradlew.bat bootRun
```

Note: If inconnected database, excuting will be around 80%, don't worry

- Linux

Generate ckqa.thrift

```shell script
generate.sh
```

Run server

```shell script
gradlew.sh run
(if failed)
gradlew.sh bootRun
```

Note: If inconnected database, excuting will be around 80%, don't worry


# Trivial
- config interface access

See config/SecurityConfiguration.java


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

- Export data using Kibana

Go to Stack Management/Index patterns/Create index pattern, create an index pattern for it, then go to discover, find the index pattern

<img src="C:\Users\wuping\AppData\Roaming\Typora\typora-user-images\image-20210330171811802.png" alt="image-20210330171811802" style="zoom:50%;" />

Click share, generate CSV file. You can find the outcome in Stack Management/Reporting.

- Get filed name and value using reflection

```java
Field[] declaredFields = clazz.getDeclaredFields();
field.getName();
field.setVisible(true); // For private field
field.get(object);
```

- Concatenate file name

```java
String.valueOf(Paths.get("foo", "bar"));
```

- Generic method

```java
public <T> void method(List<T> clazz) {}
```

- Read text file by line

```java

```

- Elasticseach cheatsheet

Get all indices

```shell
curl -X GET "localhost:9200/*?pretty"
```

Get all documents

```shell
curl -X GET "localhost:9200/<index>/_search?pretty" -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_all": {}
    }
}
'
```

Delete index

```shell
curl -X DELETE "localhost:9200/<index>?pretty"
```

Delete all documents with an index

```shell
curl -X POST "localhost:9200/<index>/_delete_by_query" -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_all": {}
    }
}
'
```

- To create `cskg` index: search with case-insensitive exact text match

```json
PUT cskg
{
  "settings": {
    "analysis": {
      "analyzer": {
        "case_insensitive_analyzer": {
          "type": "custom",
          "filter": [
            "lowercase"
          ],
          "tokenizer": "keyword"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "subject": {
        "type": "text",
        "analyzer": "case_insensitive_analyzer"
      },
      "relation": {
        "type": "text",
        "analyzer": "case_insensitive_analyzer"
      },
      "object": {
        "type": "text",
        "analyzer": "case_insensitive_analyzer"
      }
    }
  }
}
```

- To create `cskg_vector` index: search with case-insensitive exact text match and knn

```json
PUT cskg_vector
{
  "settings": {
    "analysis": {
      "analyzer": {
        "case_insensitive_analyzer": {
          "type": "custom",
          "filter": [
            "lowercase"
          ],
          "tokenizer": "keyword"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "subject": {
        "type": "text",
        "analyzer": "case_insensitive_analyzer"
      },
      "relation": {
        "type": "text",
        "analyzer": "case_insensitive_analyzer"
      },
      "object": {
        "type": "text",
        "analyzer": "case_insensitive_analyzer"
      },
      "vector": {
        "type": "dense_vector",
        "dims": 384,
        "index": false
      }
    }
  }
}
```

- Extend disk on ubuntu

https://www.cnblogs.com/wangxingggg/articles/6846834.html

```shell
sudo lsblk # Display real disk space

sudo resize2fs /dev/ubuntu-vg/root # Recalculate the disk space if `df -h` display a smaller size than real size
```

# Bugs

- Not compatible with the latest `thrift`

Use `thrift@0.9` instead

```shell
brew install thrift@0.9
echo 'export PATH="/opt/homebrew/opt/thrift@0.9/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

thrift --version
```

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

