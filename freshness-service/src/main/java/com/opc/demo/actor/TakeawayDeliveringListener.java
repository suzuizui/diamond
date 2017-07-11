package com.opc.demo.actor;

import com.wormpex.inf.wmq.api.Message;
import com.wormpex.inf.wmq.api.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @auther: lang.he
 * @date: 2017-04-06
 */
@Component("takeawayDeliveringListener")
public class TakeawayDeliveringListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        System.out.println("orderCreatedListener:" + message.toString());
    }
}
