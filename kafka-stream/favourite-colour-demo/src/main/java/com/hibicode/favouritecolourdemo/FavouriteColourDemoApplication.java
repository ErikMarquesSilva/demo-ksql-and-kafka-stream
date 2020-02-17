package com.hibicode.favouritecolourdemo;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.Arrays;

@SpringBootApplication
@EnableBinding(FavouriteColourProcessor.class)
public class FavouriteColourDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FavouriteColourDemoApplication.class, args);
	}

	@StreamListener("input")
	@SendTo("userKeysAndColours")
	public KStream<String, String> handle(KStream<String, String> input) {
		return input
				.filter((key, value) -> value.contains(","))
				.selectKey((key, value) -> value.split(",")[0].toLowerCase())
				.mapValues(value -> value.split(",")[1].toLowerCase())
				.filter((user, colour) -> Arrays.asList("green", "red", "blue").contains(colour));
	}

//		return input
//				.filter((userId, colour) -> colour.matches("blue|green|red"))
//				.selectKey((userId, colour) -> colour)
//				.groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
//				.count()
//				.toStream();

//	}

}
