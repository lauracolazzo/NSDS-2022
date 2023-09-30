# Evaluation lab - Contiki-NG

## Group number: 12

## Group members

- Laura Colazzo
- Alessandro Meroni
- Filippo Giovanni Del Nero

## Solution description
Communication starts from clients. They register a udp connection and after their one-minute timeout expires they search for the root of the RPL tree. If found, they send a temperature reading to it. Server side, a udp connection is registered too. When the server receives clients’ messages, the udp callback is called. Inside this function the ip address from whom the message arrived is compared with the ones stored locally. If the address is new to the server and the number of ip addresses already stored is less than the MAX_RECEIVERS, the address is saved. Then temperature readings are stored locally and the average among the last MAX_READINGS is computed. If the average exceeds the ALERT_THRESHOLD, clients registered so far (that are the ones whose ip address we previously stored) are notified.
The idea behind this last part of the communication is that notifications to clients need to be sent only to those that are part of the group of clients known by the server and not to all the other nodes that may be present out there. For this reason, since unicast and broadcast are both not applicable in our case, we went for a multicast connection.
