package com.eval;

import akka.actor.ActorRef;

public class InitMsg {
    private final ActorRef brokerRef;

    public InitMsg(ActorRef brokerRef) {
        this.brokerRef = brokerRef;
    }

    public ActorRef getBrokerRef() {
        return brokerRef;
    }
}
