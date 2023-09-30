package com.eval;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

public class Worker extends AbstractActor {
    private final Map<String, ActorRef> subscriptions = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(SubscribeMsg.class,this::onSubscribe)
                .match(PublishMsg.class,this::onPublish)
                .build();
    }

    void onSubscribe(SubscribeMsg msg){
        System.out.println("WORKER: Received a subscription request from: " + msg.getSender() + " to topic: " + msg.getTopic() + " with key: " + msg.getKey());
        System.out.println("WORKER: Adding entry: " + msg.getTopic() + "->" + msg.getSender() + " to subscriptions");
        subscriptions.put(msg.getTopic(),msg.getSender());

    }

    void onPublish(PublishMsg msg) throws Exception{
        if (subscriptions.containsKey(msg.getTopic())){
            System.out.println("WORKER: Forwarding event to subscriber");
            subscriptions.get(msg.getTopic()).tell(new NotifyMsg(msg.getValue()),self());
        }
        else throw new Exception("No consumers for " + msg.getTopic());

    }

    static Props props() {
        return Props.create(Worker.class);
    }
}
