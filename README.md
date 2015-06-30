# Concurrent-Programming
Producer Consumer thread problem with two cases (a semaphore and an exchanger).

Case 1: Modified the meeting room such that the room has N exchange containers. The meeting room is controlled by a semaphore. 
The semaphore will limit the number of suppliers and consumers that can be inside the meeting room at any time N.

Case 2: Threads exchange the data through an exclusive Exchanger between the consumer and the supplier.
