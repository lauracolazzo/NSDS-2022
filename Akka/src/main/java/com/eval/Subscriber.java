package com.eval;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class Subscriber extends AbstractActor {
    private ActorRef broker;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(InitMsg.class, this::init)
                .match(NotifyMsg.class, this::onNotify)
                .match(SubscribeMsg.class, this::subscribe)
                .build();
    }

    void init(InitMsg msg){
        this.broker = msg.getBrokerRef();
    }

    void onNotify(NotifyMsg msg){
        System.out.println("SUBSCRIBER: Received event: " + msg.getValue());
    }

    void subscribe(SubscribeMsg msg){
        System.out.println("SUBSCRIBER: Asking to subscribe to " + msg.getTopic());
        broker.tell(msg,self());
    }

    static Props props() {
        return Props.create(Subscriber.class);
    }
}
