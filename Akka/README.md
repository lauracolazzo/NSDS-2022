# Evaluation lab - Akka

## Group number: 12

## Group members

- Laura Colazzo
- Alessandro Meroni
- Filippo Giovanni Del Nero
  
## Assignment 
You are to create a simple event-based communication system using actors.

The system shall be composed of (at least) five actors:
- Two worker actors matching events against subscriptions
- A broker actor coordinating the operation of worker(s)
- A subscriber actor that issues subscriptions and receives notifications
- A publisher actor that generates events

The system shall use (at least) four types of messages:
- SubscribeMsg to issue subscriptions
- PublishMsg to generate events
- NotificationMsg to notify subscribers of events – BatchMsg to change the broker message policy

The broker splits the matching process between the two worker actors as follows:
- Splitting is based on a partitioning of the key attribute in the Subscribe message
  - Even keys go to one worker, odd keys go to the other
- When a worker actor receives a Publish message for
a topic it is not aware of, it fails
  - Handling the failure must ensure that the set of active subscriptions before the failure is retained
- You can assume that at most one subscriber exist for a given topic

Whenever the broker receives a BatchMsg, it looks at the isOn attribute:
- If it is false, the broker shall immediately process every subsequent message it receives
- If it is true, the broker shall buffer all event messages it receives since that time, and process them in a batch as soon as it receives another BatchMsg with isOn set to false

In the assignment, you also find:
- A definition of the four basic message types
  - You are free to extend the message definitions, but you cannot change the existing code
  - You can of course define more message types, if needed
- A template for a test main method

## Solution - Description of message flows
- Communication starts by telling the broker the properties of the two actors he needs to instantiate and that he will supervise. 
- Then an InitMsg is sent from the main method to both the publisher and subscriber with a reference to the Broker actor.
- Subscription requests are triggered from the main method. 
- Upon the receipt of a SubscribeMsg the Subscriber actor forwards that message to the Broker.
- When the Broker actor receives a SubscribeMsg it checks key’s parity and sends the message to the right Worker actor. 
- Once a worker receives the message it adds an entry to its own subscriptions map.
- Publish events are generated from the main method and sent to the Publisher actor, which forwards them to the Broker actor. 
- Then the Broker itself forwards the message to both the even and odd workers, but only if his current behaviour is not batching, otherwise it stashes them. 
- Each worker checks if it has an entry corresponding to the message’s topic and in case he doesn’t it throws an Exception and fails.
- Otherwise he retrieves the subscriber to the given topic and sends it a NotifyMsg, with the event description.
- A BatchMsg is sent from the main method to the Broker actor that processes it and possibly changes its behaviour.


