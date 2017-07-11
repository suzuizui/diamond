package com.opc.demo.actor;

import com.wormpex.inf.order.actor.api.ActorEngineDataLoader;
import org.springframework.stereotype.Component;

/**
 * Created by perry on 16/12/26.
 */
@Component
public class OrderDataLoader implements ActorEngineDataLoader<String> {
    @Override
    public String apply(String input) {
        return input;
    }
}
