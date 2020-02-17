package com.hibicode.favouritecolourdemo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

@EnableBinding(TableProcessor.class)
public class TableCounter {

	@StreamListener("userColour")
	@SendTo("favouriteColour")
	public KStream<String, Long> handle2(KTable<String, String> input) {
		return input
				.groupBy((user, colour) -> new KeyValue<>(colour, colour), Grouped.with(Serdes.String(), Serdes.String()))
				.count(Materialized.with(Serdes.String(), Serdes.Long()))
				.toStream();

	}

}
