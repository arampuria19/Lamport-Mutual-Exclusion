import model.PeerInformation;
import model.PriorityQueueElement;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LamportMutualExclusion {
    private final String localIpAddress;
    private final int listeningPort;
    private final List<PeerInformation> peerInformationList;

    private final ServerSocket serverSocket;

    private AtomicInteger timeStamp;

    private final Queue<Thread> threads;

    private int replies;

    private final PriorityQueue<PriorityQueueElement> pq;

    public LamportMutualExclusion(String localIpAddress, int listeningPort) {
        this.localIpAddress = localIpAddress;
        this.listeningPort = listeningPort;
        this.peerInformationList = new ArrayList<>();
        this.timeStamp = new AtomicInteger();
        this.threads = new LinkedList<>();
        this.replies = 0;
        this.pq = new PriorityQueue<>();

        try {
            int backlog = 5;
            this.serverSocket = new ServerSocket(listeningPort, backlog);
        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addPeerInformation(PeerInformation peerInformation) {
        this.peerInformationList.add(peerInformation);
    }

    private boolean canGoCritical() {
        assert pq.peek() != null;
        return ((replies == peerInformationList.size()) && (pq.peek().getPeerInformation().getIpAddress().equals(this.localIpAddress)));
    }

    private void executeCS() {
        System.out.println("Got Inside Critical Section!!!");
        try {
            Thread.sleep(10000); // sleep for 8 seconds
        } catch (InterruptedException e) {
            System.out.println("Error while making thread sleep for critical section: " + e.getMessage());
            throw new RuntimeException(e);
        }

        for (PeerInformation peerInformation: this.peerInformationList) {
            try (
                    Socket clientSocket = new Socket(peerInformation.getIpAddress(), peerInformation.getPort());
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
//                String sendingString = "o\n" + 0 + "\n" + this.listeningPort + "\n" + this.localIpAddress + "\n";
//                output.println(sendingString);
                output.println("o");
                output.println(0);
                output.println(this.listeningPort);
                output.println(this.localIpAddress);

                System.out.println("Release message sent to " + peerInformation);
            } catch (IOException e) {
                System.out.println("Error while sending...: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private void startAccept(Socket socket) throws IOException {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("Error while making thread sleep: " + e.getMessage());
            throw new RuntimeException(e);
        }

        int receivedTimestamp = -1;
        int port = -1;
        String ipAddress = "";
        boolean isReply = false;
        boolean isReleased = false;


        try (BufferedReader readerForLine = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while((line = readerForLine.readLine()) != null) {
                if(!line.isEmpty()) {
                    if (line.charAt(0) == 'r') { //reply
                        isReply = true;
                    } else if (line.charAt(0) == 'o') { //open
                        isReleased = true;
                    } else if(line.charAt(0) == 'q') { //request
                        continue;
                    } else if (receivedTimestamp == -1) {
                        receivedTimestamp = Integer.parseInt(line);
                    } else if(port == -1){
                        port = Integer.parseInt(line);
                    } else {
                        ipAddress = line;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading the received data: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Compare the received timestamps with the current timestamps
        if (isReply) {
            if(port == listeningPort) {
                replies++;
                System.out.println("Got a Reply!");

                if(canGoCritical()) {
                    replies = 0;
                    pq.poll();

                    new Thread(this::executeCS).start();
                }
            }
        } else if(isReleased) {
            pq.poll();
            System.out.println("Got a Release message!");
            if(canGoCritical()) {
                replies = 0;
                pq.poll();
                new Thread(this::executeCS).start();
            }
        } else {
            timeStamp = new AtomicInteger(Math.max(timeStamp.get(), receivedTimestamp) + 1);

            System.out.println("Got a Request from " + ipAddress + ": " + port);

            pq.add(new PriorityQueueElement(timeStamp.get(), new PeerInformation(ipAddress, port)));

            try (
                    Socket clientSocket = new Socket(ipAddress, port);
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                    ) {
//                String sendingString = "r\n" + timeStamp.get() + "\n" + port + "\n" + ipAddress + "\n";
//                output.println(sendingString);
                output.println("r");
                output.println(timeStamp.get());
                output.println(port);
                output.println(ipAddress);

                System.out.println("Reply sent to " + ipAddress + ": " + port);
            } catch (IOException e) {
                System.out.println("Error while sending...: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        socket.close();
    }

    public void startListening() {
        final ServerSocket finalServerSocket = serverSocket;
        Thread currentThread = new Thread(() -> {
            while(true) {
                try {
                    startAccept(finalServerSocket.accept());
                } catch (IOException e) {
                    System.out.println("Error while accepting connection: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        currentThread.start();
        threads.add(currentThread);
    }

    public void startEventGeneration() {

        Thread currentThread = new Thread(() -> {
            while(true) {
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter 1 to request Critical Section, or \n" +
                        "2 to print current queue or \n" +
                        "3 to exit the code.\n");

                int option = scanner.nextInt();

                if (option == 1) {
                    // Try to enter the critical section
                    timeStamp.addAndGet(1);

                    pq.add(new PriorityQueueElement(timeStamp.get(), new PeerInformation(localIpAddress, listeningPort)));

                    for (PeerInformation peerInformation: peerInformationList) {
                        try (
                                Socket clientSocket = new Socket(peerInformation.getIpAddress(), peerInformation.getPort());
                                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                        ) {
//                            String sendingString = "q\n" + timeStamp.get() + "\n" + listeningPort + "\n" + localIpAddress + "\n";
//                            output.println(sendingString);
                            output.println("q");
                            output.println(timeStamp.get());
                            output.println(listeningPort);
                            output.println(localIpAddress);

                            System.out.println("Request sent to " + peerInformation.getIpAddress() + ": " + peerInformation.getPort());
                        } catch (IOException e) {
                            System.out.println("Error while sending...: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                } else if(option == 2) {
                    for (PriorityQueueElement priorityQueueElement : pq) {
                        System.out.println(priorityQueueElement);
                    }
                } else if(option == 3) {
                    System.exit(0);
                }
            }
        });

        currentThread.start();
        threads.add(currentThread);
    }

   public void waitForThreads() {

       for (Thread thread : threads) {
           try {
               thread.join();
           } catch (InterruptedException e) {
               System.out.println("Error while joining the threads: ");
               throw new RuntimeException(e);
           }
       }
   }

}
