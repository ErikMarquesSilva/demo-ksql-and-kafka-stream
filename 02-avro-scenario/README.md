# Scenario with JSON

## Producing messages

### Recharge

````json
{
    "amount": "1000",
    "client_id": "1111"
}
````

````
ksql-datagen schema=./datagen/recharges.avro format=avro topic=recharge key=client_id maxInterval=5000 iterations=10000
````

Producing one by one:

```
kafka-avro-console-producer \
    --broker-list localhost:9092 --topic recharge \
    --property parse.key=true \
    --property key.schema='{"type":"string"}' \
    --property key.separator=: \
    --property value.schema='{"type":"record","name":"KsqlDataSourceSchema","namespace": "io.confluent.ksql.avro_schemas","fields":[{"name":"amount","type":"string"}, {"name":"client_id","type":"string"}]}'
"1111":{"amount":"300", "client_id":"1111"}
"2222":{"amount":"340", "client_id":"2222"}
````

### Client

````json
{
    "id": "1111",
    "phone_number": "11988881111",
    "name": "Marcos Vieira"
}
````

````
ksql-datagen schema=./datagen/clients.avro format=avro topic=client key=id maxInterval=5000 iterations=10000
````

Producing one by one:

````
kafka-avro-console-producer --broker-list localhost:9092 --topic client \
    --property parse.key=true \
    --property key.schema='{"type":"string"}' \
    --property key.separator=: \
    --property value.schema='{"type":"record","name":"KsqlDataSourceSchema","namespace": "io.confluent.ksql.avro_schemas","fields":[{"name":"id","type":"string"}, {"name":"phone_number","type":"string"}, {"name":"name","type":"string"}]}'
"1111":{"id":"1111", "phone_number":"11988881111", "name":"Marcos Vieira"}
"1111":{"id":"2222", "phone_number":"11988882222", "name":"Renato Silva"}
````

## Streaming

### KSQL

````
CREATE STREAM RECHARGE_WRAPPER WITH (KAFKA_TOPIC='recharge', VALUE_FORMAT='AVRO', KEY='client_id');

CREATE TABLE CLIENT_TABLE WITH (KAFKA_TOPIC='client', VALUE_FORMAT='AVRO', KEY='id');

CREATE STREAM PUSH_NOTIFICATION AS SELECT C.PHONE_NUMBER AS PHONE_NUMBER, C.NAME AS NAME, R.AMOUNT FROM RECHARGE_WRAPPER R INNER JOIN CLIENT_TABLE C ON R.CLIENT_ID = C.ID;

SELECT TIMESTAMPTOSTRING(windowStart(), 'HH:mm:ss')
  ,TIMESTAMPTOSTRING(WindowEnd(), 'HH:mm:ss'), NAME, SUM(CAST(AMOUNT AS INTEGER)) AS TOTAL FROM PUSH_NOTIFICATION WINDOW TUMBLING (size 60 seconds) GROUP BY NAME EMIT CHANGES;
````

Join Event Stream with KSQL https://docs.confluent.io/current/ksql/docs/developer-guide/join-streams-and-tables.html

KSQL CLI Syntax reference: https://docs.confluent.io/current/ksql/docs/developer-guide/syntax-reference.html

### Updating messages

Update topic *recharge* with the follow message:

````
"1111":{"value":"300", "client_id":"1111"}
````

### Update schema:

````
kafka-avro-console-producer \
    --broker-list localhost:9092 --topic recharge \
    --property parse.key=true \
    --property key.schema='{"type":"string"}' \
    --property key.separator=: \
    --property value.schema='{"type":"record","name":"KsqlDataSourceSchema","namespace": "io.confluent.ksql.avro_schemas","fields":[{"name":"amount","type":"string"}, {"name":"client_id","type":"string"}, {"name":"value","type":"string"}]}'
"1111":{"value":"300", "client_id":"1111", "amount":"300"}
````

````
kafka-avro-console-producer \
    --broker-list localhost:9092 --topic recharge \
    --property parse.key=true \
    --property key.schema='{"type":"string"}' \
    --property key.separator=: \
    --property value.schema='{"type":"record","name":"KsqlDataSourceSchema","namespace": "io.confluent.ksql.avro_schemas","fields":[{"name":"amount","type":"string"}, {"name":"client_id","type":"string"}, {"name":"value","type":"string","default":"0"}]}'
"1111":{"value":"300", "client_id":"1111", "amount":"300"}
````
