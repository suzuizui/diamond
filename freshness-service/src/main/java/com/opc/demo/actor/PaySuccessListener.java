package com.opc.demo.actor;

import com.wormpex.inf.wmq.api.Message;
import com.wormpex.inf.wmq.api.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Created by caohanzhen on 17/1/7.
 */
@Component("paySuccessListener")
public class PaySuccessListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println("orderCreatedListener:" + message.toString());
    }
}
