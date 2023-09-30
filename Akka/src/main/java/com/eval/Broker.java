package com.eval;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;

import java.time.Duration;

public class Broker extends AbstractActorWithStash {
    private ActorRef evenWorker;
    private ActorRef oddWorker;

    private boolean isOn = false;

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(
                    1, // Max no of retries
                    Duration.ofMinutes(1), // Within what time period
                    DeciderBuilder.match(Exception.class, e -> SupervisorStrategy.resume())
                            .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public Receive createReceive() {
        return notBatching();
    }

    private final Receive batching() {
        return receiveBuilder()
                .match(
                        Props.class,
                        props -> {
                            evenWorker = getContext().actorOf(props);
                            oddWorker = getContext().actorOf(props);
                        })
                .match(PublishMsg.class,this::buffer)
                .match(SubscribeMsg.class,this::onSubscribe)
                .match(BatchMsg.class,this::onBatch)
                .build();
    }

    private final Receive notBatching() {
        return receiveBuilder()
                .match(
                        Props.class,
                        props -> {
                            evenWorker = getContext().actorOf(props);
                            oddWorker = getContext().actorOf(props);
                        })
                .match(PublishMsg.class,this::onPublish)
                .match(SubscribeMsg.class,this::onSubscribe)
                .match(BatchMsg.class,this::onBatch)
                .build();
    }

    private void buffer(PublishMsg msg){
        System.out.println("BROKER: Stashing event message..");
        stash();
    }

    void onPublish(PublishMsg msg){
        System.out.println("BROKER: Received an event on " + msg.getTopic() + " with value: " + msg.getValue());

        System.out.println("BROKER: Forwarding event message to all workers..");
        evenWorker.tell(msg,self());
        oddWorker.tell(msg,self());
    }

    void onSubscribe(SubscribeMsg msg){
        System.out.println("BROKER: Received a subscription request from: " + msg.getSender() + " to " + msg.getTopic() + " with key: " + msg.getKey());
        if (msg.getKey() % 2 == 0){
            System.out.println("BROKER: Sending subscription to even worker");
            evenWorker.tell(msg,msg.getSender());
        }
        else {
            System.out.println("BROKER: Sending subscription to odd worker");
            oddWorker.tell(msg,msg.getSender());
        }
    }

    void onBatch(BatchMsg msg){
        System.out.println("BROKER: Received a batch message with parameter isOn = " + msg.isOn());

        if (!msg.isOn()){
            getContext().become(notBatching());
            System.out.println("BROKER: Unstashing all messages..");
            unstashAll();
        }
        else {
            getContext().become(batching());
        }
    }
    static Props props() {
        return Props.create(Broker.class);
    }
}
