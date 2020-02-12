package kafka.stream.demo.processor

import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.messaging.handler.annotation.SendTo


//@EnableBinding(Processor::class)
//class UppercaseProcessor {
//
//    @StreamListener(Processor.INPUT)
//    @SendTo(Processor.OUTPUT)
//    fun process(s: String): String {
//        return s.toUpperCase()
//    }
//}

