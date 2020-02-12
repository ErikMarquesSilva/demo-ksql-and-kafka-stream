package kafka.stream.demo.processor

import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.handler.annotation.SendTo


@EnableBinding(StreamTableProcessor::class)
class KafkaStreamsTableJoin {

    @StreamListener
    @SendTo("output")
    fun process(
        @Input("recharge") recharge: KStream<Any?, Recharge>,
        @Input("client") client: KTable<Any?, Any>
    ): KStream<*, *> {

        return recharge
    }

    data class Recharge(
        val amount: String = "",
        val clientId: String = ""
    )
}
