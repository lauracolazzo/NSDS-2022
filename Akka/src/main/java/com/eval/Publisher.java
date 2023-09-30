package com.eval;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class Publisher extends AbstractActor {
    private ActorRef broker;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(InitMsg.class, this::init)
                .match(PublishMsg.class, this::publish)
                .build();
    }

    void init(InitMsg msg){
        this.broker = msg.getBrokerRef();
    }

    void publish(PublishMsg msg){
        System.out.println("PUBLISHER: Publishing event on " + msg.getTopic() + " with value: " + msg.getValue());
        broker.tell(msg,self());
    }
    static Props props() {
        return Props.create(Publisher.class);
    }
}
