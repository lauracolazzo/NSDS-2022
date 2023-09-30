# Evaluation lab - Apache Kafka

## Group number: 12

## Group members

- Laura Colazzo
- Filippo Giovanni Del Nero
- Alessandro Meroni

## Exercise 1

- Number of partitions allowed for inputTopic (k, N)
  
- Number of consumers allowed (1, N)
    - Consumer 1: <groupId, t1>
    - Consumer 2: <groupId, t2>
    - ...
    - Consumer n: <groupId, tn>
  
- k is the number assigned to TopicManager's attribute "defaultTopicPartitions". k must be greater than or equal to the number of consumers in each group.
- the groupId has to be the same for each consumer in order to not duplicate the values in the outputTopic, supposing that a single outputTopic exists.
  
## Exercise 2

- Number of partitions allowed for inputTopic (1, N)
- Number of consumers allowed (1, N)
    - Consumer 1: \<groupId2>
    - Consumer 2: \<groupId3>
    - ...
    - Consumer n: \<groupIdn>
- There must be a single consumer per group otherwise the average wouldn't be computed correctly because data would be split between group members. 
Multiple logical consumers (consumers with different groupId) are allowed but they would be redundant.
- In order to run both the FilterForwarder and the AverageConsumer at the same time we assign them to different groups so that they both pull from the entire inputTopic
