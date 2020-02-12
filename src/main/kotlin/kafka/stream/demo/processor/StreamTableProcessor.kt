package kafka.stream.demo.processor

import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output

interface StreamTableProcessor {
    @Input("recharge")
    fun recharge(): KStream<*, *>

    @Output("output")
    fun output(): KStream<*, *>

    @Input("client")
    fun client(): KTable<*, *>
}