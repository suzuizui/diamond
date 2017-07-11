package com.opc.demo.actor;

import com.google.common.collect.Maps;
import com.wormpex.inf.order.actor.api.ActorMessage;
import com.wormpex.inf.order.actor.api.OrderActor;
import com.wormpex.inf.order.actor.api.QActor;
import com.wormpex.inf.order.actor.api.QDiff;
import com.wormpex.inf.order.actor.api.event.common.DiffEvent;
import com.wormpex.inf.wmq.api.Message;
import com.wormpex.inf.wmq.api.MessageBuilder;
import com.wormpex.inf.wmq.api.MessageProducer;
import org.apache.commons.collections.MapUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by caohanzhen on 17/1/9.
 */
@QActor
@QDiff(section = "order-transport", path = "transportStatus.name", newValue = "Checking")
public class CheckingMessageActor implements OrderActor<String, DiffEvent> {
    @Resource
    private MessageProducer messageProducer;

    @Override
    public void onMessage(ActorMessage<String, DiffEvent> message) {
        String orderData = message.orderData();
        sendMessage(1, 1, 1L * 60 * 1000, "uuuu", Maps.newHashMap());
    }

    private void sendMessage(long orderId, int version, long delay, String subject, Map<String, String> params) {
        MessageBuilder builder = MessageBuilder.create()
                .withDelayMills((int) delay)
                .withTopic(subject);

        if (MapUtils.isNotEmpty(params)) {
            params.keySet().stream().forEach(key -> builder.withAttr(key, params.get(key)));
        }

        Message message = builder.build();
        messageProducer.send(message);
    }
}
