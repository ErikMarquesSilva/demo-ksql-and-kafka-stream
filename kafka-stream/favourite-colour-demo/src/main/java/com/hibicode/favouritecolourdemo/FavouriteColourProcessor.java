package com.hibicode.favouritecolourdemo;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface FavouriteColourProcessor {

    @Input
    KStream<String, String> input();

    @Output
    KStream<String, String> userKeysAndColours();

}
