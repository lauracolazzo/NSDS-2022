# Evaluation lab - Akka

## Group number: 12

## Group members

- Laura Colazzo
- Alessandro Meroni
- Filippo Giovanni Del Nero

## Description of message flows
Communication starts by telling the broker the properties of the two actors he needs to instantiate and that he will supervise. 
Then an InitMsg is sent from the main method to both the publisher and subscriber with a reference to the Broker actor.
Subscription requests are triggered from the main method. 
Upon the receipt of a SubscribeMsg the Subscriber actor forwards that message to the Broker.
When the Broker actor receives a SubscribeMsg it checks key’s parity and sends the message to the right Worker actor. 
Once a worker receives the message it adds an entry to its own subscriptions map.
Publish events are generated from the main method and sent to the Publisher actor, which forwards them to the Broker actor. 
Then the Broker itself forwards the message to both the even and odd workers, but only if his current behaviour is not batching, otherwise it stashes them. 
Each worker checks if it has an entry corresponding to the message’s topic and in case he doesn’t it throws an Exception and fails.
Otherwise he retrieves the subscriber to the given topic and sends it a NotifyMsg, with the event description.
A BatchMsg is sent from the main method to the Broker actor that processes it and possibly changes its behaviour.


