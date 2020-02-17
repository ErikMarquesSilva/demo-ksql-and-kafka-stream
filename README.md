# POC Kafka Stream/KSQL e Kafka Connect


## Startup via Configure Confluent Platform

Reference https://docs.confluent.io/current/quickstart/ce-quickstart.html#ce-quickstart

Confluent local https://docs.confluent.io/current/cli/command-reference/confluent-local/index.html

````
export CONFLUENT_HOME=<path-to-confluent>
export PATH=$CONFLUENT_HOME/bin:$PATH


curl -L https://cnfl.io/cli | sh -s -- -b /<path-to-cli>
````

Make sure that \<path-to-cli> is something like: /usr/local/bin

````
curl -L https://cnfl.io/cli | sh -s -- -b /usr/local/bin
````

Perhaps is a Confluent bug, but you have to create a file:
````
mkdir $HOME/.confluent
echo '{}' > $HOME/.confluent/config.json
````

### Start servers

````
confluent local start ksql-server
````

## Startup via Docker

Use the docker-compose.yml. Reference https://docs.confluent.io/current/quickstart/ce-docker-quickstart.html#ce-docker-quickstart

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

````
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
kafka-avro-console-producer \
    --broker-list localhost:9092 --topic client \
    --property value.schema='{"type":"record","name":"client","fields":[{"name":"id","type":"string"}, {"name":"phone_number","type":"string"}, {"name":"name","type":"string"}]}'
"1111":{"id":"1111", "phone_number":"11988881111", "name":"Marcos Vieira"}
"2222":{"id":"2222", "phone_number":"11988882222", "name":"Renato Silva"}
````

## Streaming

### KSQL

````
CREATE STREAM RECHARGE_WRAPPER WITH (KAFKA_TOPIC='recharge', VALUE_FORMAT='AVRO');

CREATE TABLE CLIENT_TABLE WITH (KAFKA_TOPIC='client', VALUE_FORMAT='AVRO', KEY='id');

CREATE STREAM PUSH_NOTIFICATION AS SELECT C.PHONE_NUMBER AS PHONE_NUMBER, C.NAME AS NAME, R.AMOUNT FROM RECHARGE_WRAPPER R INNER JOIN CLIENT_TABLE C ON R.CLIENT_ID = C.ID;

SELECT TIMESTAMPTOSTRING(windowStart(), 'HH:mm:ss')
  ,TIMESTAMPTOSTRING(WindowEnd(), 'HH:mm:ss'), NAME, SUM(CAST(AMOUNT AS INTEGER)) AS TOTAL FROM PUSH_NOTIFICATION WINDOW TUMBLING (size 60 seconds) GROUP BY NAME EMIT CHANGES;
````

Join Event Stream with KSQL https://docs.confluent.io/current/ksql/docs/developer-guide/join-streams-and-tables.html

KSQL CLI Syntax reference: https://docs.confluent.io/current/ksql/docs/developer-guide/syntax-reference.html

### Kafka Stream

KSQL and Kafka Stream https://docs.confluent.io/current/ksql/docs/concepts/ksql-and-kafka-streams.html

## Schema Evolution and Compatibility

https://docs.confluent.io/current/schema-registry/avro.html


## Passos para cada solução - o que o dev precisa fazer?
 * Descrever aqui com um pouco mais de detalhes, talvez em desenho, o que precisa ser feito de deploy para colocar no ar a solução Kafka Stream ou KSQL.
 
 

### Kafka Stream Application

````
kafka-console-producer --broker-list localhost:9092 --topic recharge --property parse.key=true --property key.separator=:
````


Consume message of KStream
````
kafka-console-consumer --bootstrap-server localhost:9092 --topic recharge --from-beginning --group KafkaStreamsTableJoin-process-applicationId
````
