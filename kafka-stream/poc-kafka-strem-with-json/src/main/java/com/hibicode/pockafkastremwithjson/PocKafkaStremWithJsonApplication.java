package com.hibicode.pockafkastremwithjson;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
@EnableBinding(RechargeProcessor.class)
public class PocKafkaStremWithJsonApplication {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

	public static void main(String[] args) {
		SpringApplication.run(PocKafkaStremWithJsonApplication.class, args);
	}

	@StreamListener
	@SendTo("output")
	public KStream<String, TotalPerClient> handle(@Input("input") KStream<String, Recharge> input
			, @Input("inputTable") KTable<String, Client> inputTable) {

		return input
				.leftJoin(inputTable
						, (rechage, client) -> new ValuePerClient(client.getName(), Long.valueOf(rechage.getAmount())))
				.groupByKey()
				.windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
				.aggregate(() -> new TotalPerClient()
						, (aggKey, newValue, aggValue) -> new TotalPerClient(newValue.name, newValue.value + aggValue.getTotal())
						, Materialized.with(Serdes.String(), getTotalPerClientSerde()))
				.toStream()
				.map((key, value)
						-> new KeyValue<String, TotalPerClient>(
						formatter.format(key.window().startTime()) + " - " + formatter.format(key.window().endTime())
						, new TotalPerClient(value.getName(), value.getTotal())));
	}

//		return input
//				.leftJoin(inputTable
//						, (rechage, client) -> new ValuePerClient(client.getName(), Long.valueOf(rechage.getAmount())))
//				.groupByKey()
//				.windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
//				.aggregate(() -> 0L
//						, (aggKey, newValue, aggValue) -> aggValue + newValue.getValue()
//						, Materialized.with(Serdes.String(), Serdes.Long()))
//				.toStream()
//				.map((key, value) -> new KeyValue<>(null, new TotalPerClient(key.key(), value)));

//	}

	private Serde<TotalPerClient> getTotalPerClientSerde() {
		Map<String, Object> serdeProps = new HashMap<>();

		final Serializer<TotalPerClient> totalPerClientSerializer = new JsonPOJOSerializer<>();
		serdeProps.put("JsonPOJOClass", TotalPerClient.class);
		totalPerClientSerializer.configure(serdeProps, false);

		final Deserializer<TotalPerClient> totalPerClientDeserializer = new JsonPOJODeserializer<>();
		serdeProps.put("JsonPOJOClass", TotalPerClient.class);
		totalPerClientDeserializer.configure(serdeProps, false);

		return Serdes.serdeFrom(totalPerClientSerializer, totalPerClientDeserializer);
	}

	public static class ValuePerClient {
		private String name;
		private Long value;

		public ValuePerClient() {
		}

		public ValuePerClient(String name, Long value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getValue() {
			return value;
		}

		public void setValue(Long value) {
			this.value = value;
		}
	}

}
