# Evaluation lab - Apache Kafka
## Group number: 12

## Group members

- Laura Colazzo
- Filippo Giovanni Del Nero
- Alessandro Meroni

## Assumptions
One instance of the Producer class publishes messages into the topic “inputTopic”
- The producer is idempotent 
- Message keys are String
- Message values are Integer

You may set the number of partitions for “inputTopic” using the TopicManager class
- Indicate the minimum and maximum number of partitions allowed

Consumers take in input at least one argument – The first one is the consumer group
- You may add any other argument you need

## Exercise 1
### Task 
Implement a FilterForwarder
- It consumes messages from “InputTopic”
- It forwards messages to “OutputTopic”
- It forwards only messages with a value greater than
threshold (which is an attribute of the class)
- It provides exactly once semantics
  - All messages that overcome the threshold need to be forwarded to once and only once

### Solution
- Number of partitions allowed for inputTopic (k, N)
  
- Number of consumers allowed (1, N)
    - Consumer 1: <groupId, t1>
    - Consumer 2: <groupId, t2>
    - ...
    - Consumer n: <groupId, tn>
  
- k is the number assigned to TopicManager's attribute "defaultTopicPartitions". k must be greater than or equal to the number of consumers in each group.
- the groupId has to be the same for each consumer in order to not duplicate the values in the outputTopic, supposing that a single outputTopic exists.
  
## Exercise 2
### Task 
Implement a AverageConsumer
- It computes the average value across all keys (i.e., the sum of the last value received for all keys divided by the number of keys)
- It prints the average value every time it changes
- It does NOT provide guarantees in the case of failures
  - Input messages may be lost or considered more than once in the case of failures
  - The average value may be temporarily incorrect in the case of failures

### Solution
- Number of partitions allowed for inputTopic (1, N)
- Number of consumers allowed (1, N)
    - Consumer 1: \<groupId2>
    - Consumer 2: \<groupId3>
    - ...
    - Consumer n: \<groupIdn>
- There must be a single consumer per group otherwise the average wouldn't be computed correctly because data would be split between group members. 
Multiple logical consumers (consumers with different groupId) are allowed but they would be redundant.
- In order to run both the FilterForwarder and the AverageConsumer at the same time we assign them to different groups so that they both pull from the entire inputTopic
