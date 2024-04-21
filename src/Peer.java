import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Peer {
    private int listeningPort;
    private List<Integer> peerPorts;
    private int replies = 0;
    private PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getKey));
//    private PriorityQueue<AbstractMap.SimpleEntry<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(AbstractMap.SimpleEntry::getKey));

    public Peer(int listeningPort, List<Integer> peerPorts) {
        this.listeningPort = listeningPort;
        this.peerPorts = peerPorts;
    }

    public boolean canGoCritical() {
        return replies == peerPorts.size() - 1 && pq.peek().getValue() == listeningPort;
    }

    public void executeCS() {
        System.out.println("GOT INSIDE CRITICAL SECTION!");
        try {
            Thread.sleep(8000); // sleep for 8 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int port : peerPorts) {
            if (port == listeningPort) continue;
            try (Socket socket = new Socket("localhost", port)) {
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println("o");
                writer.println(0);
                writer.println(listeningPort);
                System.out.println("Release message sent to " + port);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void startAccept(ServerSocket serverSocket) {
        try (Socket clientSocket = serverSocket.accept()) {
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = reader.readLine()) != null) {
                // Process the message
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void startListening() {
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            while (true) {
                startAccept(serverSocket);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void startEventGeneration() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter 1 to request Critical Section, or \n2 to print current queue or \n3 to exit the code.");
            int option = scanner.nextInt();

            if (option == 1) {
                // Request critical section
            } else if (option == 2) {
                // Print current queue
            } else if (option == 3) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the listening port for this process: ");
        int listeningPort = scanner.nextInt();

        System.out.print("Enter the number of peers (excluding this): ");
        int numPeers = scanner.nextInt();

        List<Integer> peerPorts = new ArrayList<>();
        for (int i = 0; i < numPeers; i++) {
            System.out.print("Enter the port for peer " + (i + 1) + ": ");
            peerPorts.add(scanner.nextInt());
        }

        peerPorts.add(listeningPort);
        Collections.sort(peerPorts);

        Peer peer = new Peer(listeningPort, peerPorts);
        peer.startListening();
        peer.startEventGeneration();
    }
}
