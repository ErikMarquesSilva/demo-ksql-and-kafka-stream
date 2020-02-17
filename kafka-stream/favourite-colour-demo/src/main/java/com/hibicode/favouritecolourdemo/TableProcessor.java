package com.hibicode.favouritecolourdemo;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface TableProcessor {

    @Input
    KTable<String, String> userColour();

    @Output
    KStream<String, Long> favouriteColour();
}
