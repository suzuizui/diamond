package com.opc.demo.actor;

import com.wormpex.inf.wmq.api.Message;
import com.wormpex.inf.wmq.api.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Created by caohanzhen on 17/3/1.
 */
@Component("cartOrderChangeListener")
public class CartOrderChangeListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        System.out.println("orderCreatedListener:" + message.toString());
    }
}
