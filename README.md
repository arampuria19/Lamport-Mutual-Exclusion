# Lamport's Mutual Exclusion Implementation using Logical Clock and Sockets

## Cloud Computing Assignment

## Overview

This project aims to implement Lamport's Mutual Exclusion algorithm utilizing logical clocks. The implementation will
involve communication between N devices via sockets. The critical section is represented by access to a shared 
file on any of the three machines(The entry and exit of critical section is printed) . 
Each device will run two threads: one for generating and sending local events (specifically mutual exclusion requests), 
and the other for listening and receiving events. Synchronization between the sender and receiver threads is crucial to update the logical clock accurately.

Our implementation uses sockets to communicate using the TCP protocol to exchange the messages. These messages are of three kinds:
1. Request: A process which wants to access the critical section sends this message to every other connected peer, requesting the critical section.
2. Reply: A process responds to the request message with a reply after adding the request in it's priority queue.
3. Open: A process sends the open message when it releases the critical section.

For ordering we use timestamps produced by Lamport's logical clock. We use the timestamps to compare the elements in the priority queue.

## File Structure

```
project-root/
│
├── src/
│   ├── model/
|   |   ├── PriorityQueueElement.java    
│   │   └── PeerInformation.java
│   ├── LamportMutualExclusion.java
│   └── Main.java
│
└── README.md
```

## Instructions

NOTE: This project was made using IntelliJ IDEA, and is preferred IDE to open this project. You can compile and run the 
files directly as well, using the steps below.

### 1. Compilation

Compile the Java source files using any Java compiler, ensuring all dependencies are resolved.

```bash
javac src/*.java
```

### 2. Execution

Run the compiled `Main` class to start the Lamport Mutual Exclusion system.

```bash
java -classpath src Main
```

### 3. Input Prompts

- **Local IP Address:** Enter the IP address of the current device.
- **Listening Port:** Specify the port number to listen for incoming connections.
- **Number of Peers:** Input the count of other devices (peers) participating in the mutual exclusion system. (Other devices refers to devices other than self)

For each peer:
- **IP Address:** Enter the IP address of the peer device.
- **Port:** Specify the port number on the peer device.

### 4. Execution Flow

- The program initializes the Lamport Mutual Exclusion system with the provided local IP address and listening port.
- For each peer, it creates a `PeerInformation` object with the IP address and port.
- The system starts listening on the specified port and begins generating events.
- Two threads per device manage local event generation and event reception.
- The sender thread creates local events and sends them as mutual exclusion requests.
- The receiver thread listens for incoming events and updates the logical clock accordingly.
- The program waits for all threads to complete execution before exiting.

## Dependencies

- Java Development Kit (JDK) 14 or higher.

## Notes

- Ensure that the devices are networked and reachable from each other.
- This implementation assumes a simplified scenario where access to a shared file represents the critical section (This is shown by printing of appropriate messages).
- Carefully handle exceptions related to socket communication and input validation.
- This README assumes basic familiarity with Java programming and socket communication concepts.

## Contributors

- Akshat Rampuria (20CS02013)
- Ayush Vinayak Asutkar (20CS01057)
- Kushagra Khare (20CS02004)
