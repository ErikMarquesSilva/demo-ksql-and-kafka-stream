List topics - Default Sink.class creates topic "input".
````
kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --list
````

Produce messages to topic:
````
kafka-console-producer --broker-list localhost:9092 --topic input --property parse.key=true --property key.separator=,
````
pedro,blue
maria,green
pedro,red
ricardo,red



