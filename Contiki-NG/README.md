# Evaluation lab - Contiki-NG

## Group number: 12

## Group members

- Laura Colazzo
- Alessandro Meroni
- Filippo Giovanni Del Nero

## Assignment
- You are to implement a simple sensing application
	- A UDP client sends (fake) temperature readings to a
server every ~1 minute
	- The UDP server collects the last `MAX_READINGS` temperature readings coming from clients
	- Every time a new reading is received, the server
		- Computes the average of the received temperature readings
		- If the average is above `ALERT_THRESHOLD`, it immediately notifies back-to-back all existing clients of a “high temperature alert”
	- Nothing is sent back otherwise

### Assumptions

- Clients may appear at any time, but they never disappear
- Your solution must be able to handle up to `MAX_RECEIVERS` clients, no more
- You can use COOJA motes
- Multiple calls to `simple_udp_sendto` in sequence are unlikely to succeed
	- The outgoing queue has size one

## Solution description
Communication starts from clients. They register a udp connection and after their one-minute timeout expires they search for the root of the RPL tree. If found, they send a temperature reading to it. 

Server side, a udp connection is registered too. When the server receives clients’ messages, the udp callback is called. Inside this function the ip address from whom the message arrived is compared with the ones stored locally. If the address is new to the server and the number of ip addresses already stored is less than the `MAX_RECEIVERS`, the address is saved.

Then temperature readings are stored locally and the average among the last `MAX_READINGS` is computed. If the average exceeds the `ALERT_THRESHOLD`, clients registered so far (that are the ones whose ip address we previously stored) are notified.

The idea behind this last part of the communication is that notifications to clients need to be sent only to those that are part of the group of clients known by the server and not to all the other nodes that may be present out there. For this reason, since unicast and broadcast are both not applicable in our case, we went for a multicast connection.
