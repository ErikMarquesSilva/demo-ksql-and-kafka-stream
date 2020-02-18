# Scenario with JSON

## Start Confluent Platform - Kafka

````
confluent local start ksql-server
````

## Producing messages

### Recharge

````json
{
    "amount": "1000",
    "client_id": "1111"
}
````

Creating topic

````
kafka-topics --bootstrap-server localhost:9092 --create --topic recharge --replication-factor 1 --partitions 1
````

Producing messages

````
kafka-console-producer --broker-list localhost:9092 --topic recharge
{"amount":"300", "client_id":"1111"}
{"amount":"340", "client_id":"2222"}
````

### Client

````json
{
    "id": "1111",
    "phone_number": "11988881111",
    "name": "Marcos Vieira"
}
````

Creating topic

````
kafka-topics --bootstrap-server localhost:9092 --create --topic client --replication-factor 1 --partitions 1
````


Producing messages:

````
kafka-console-producer --broker-list localhost:9092 --topic client --property parse.key=true --property key.separator=:
1111:{"id":"1111", "phone_number":"11988881111", "name":"Marcos Vieira"}
2222:{"id":"2222", "phone_number":"11988882222", "name":"Renato Silva"}
````

## Streaming

### KSQL

KSQL CLI Syntax reference: https://docs.confluent.io/current/ksql/docs/developer-guide/syntax-reference.html

If you want to see the messagens since earliest, use: *set 'auto.offset.reset' = 'earliest';* in KSQL console.

````
CREATE STREAM RECHARGE_WRAPPER (AMOUNT VARCHAR, CLIENT_ID VARCHAR) WITH (KAFKA_TOPIC='recharge', VALUE_FORMAT='JSON');

CREATE TABLE CLIENT_TABLE (ID VARCHAR, PHONE_NUMBER VARCHAR, NAME VARCHAR) WITH (KAFKA_TOPIC='client', VALUE_FORMAT='JSON', KEY='id');

CREATE STREAM PUSH_NOTIFICATION AS SELECT C.PHONE_NUMBER AS PHONE_NUMBER, C.NAME AS NAME, R.AMOUNT FROM RECHARGE_WRAPPER R INNER JOIN CLIENT_TABLE C ON R.CLIENT_ID = C.ID;

SELECT TIMESTAMPTOSTRING(windowStart(), 'HH:mm:ss')
  ,TIMESTAMPTOSTRING(WindowEnd(), 'HH:mm:ss'), NAME, SUM(CAST(AMOUNT AS INTEGER)) AS TOTAL FROM PUSH_NOTIFICATION WINDOW TUMBLING (size 60 seconds) GROUP BY NAME EMIT CHANGES;
````

Join Event Stream with KSQL https://docs.confluent.io/current/ksql/docs/developer-guide/join-streams-and-tables.html

### Updating messages

Update topic *recharge* with the follow message:

````
{"value":"300", "client_id":"1111"}
````
