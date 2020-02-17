package com.hibicode.pockafkastremwithjson;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface RechargeProcessor {

    @Input
    KStream<?, ?> input();

    @Input
    KTable<?, ?> inputTable();

    @Output
    KStream<String, TotalPerClient> output();
//    KStream<String, TotalPerClient> output();

}
