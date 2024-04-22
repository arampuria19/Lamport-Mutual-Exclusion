import model.PeerInformation;
import model.PriorityQueueElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LamportMutualExclusion {
    private final String localIpAddress;
    private final int listeningPort;
    private final List<PeerInformation> peerInformationList;

    private final ServerSocket serverSocket;

    private final AtomicInteger timeStamp;

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
            this.serverSocket = new ServerSocket(listeningPort);
        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addPeerInformation(PeerInformation peerInformation) {
        this.peerInformationList.add(peerInformation);
    }

    private boolean canGoCritical() {
        return ((replies == peerInformationList.size()) && (pq.peek().getPeerInformation().getIpAddress() == ));
    }

    private void startAccept(Socket socket) throws IOException {
        BufferedReader input = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        String receivedString = input.readLine();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("Error while making thread sleep: " + e.getMessage());
            throw new RuntimeException(e);
        }

        int receivedTimestamp = -1;
        int port = -1;
        boolean isReply = false;
        boolean isReleased = false;

        try (BufferedReader readerForLine = new BufferedReader(new StringReader(receivedString))) {
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
                    } else {
                        port = Integer.parseInt(line);
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

                    new Thread(() -> {
                        executeCS();
                    }).start();
                }
            }
        } else if(isReleased) {
            pq.poll();
            System.out.println("Got a Release message!");
            if(canGoCritical()) {
                replies =
            }

        }
    }

    public void startListening() {
        int backlog = 5;
        Thread currentThread = new Thread(() -> {
            final ServerSocket finalServerSocket = serverSocket;
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

}
