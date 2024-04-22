import model.PeerInformation;

import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the ip address for this process: ");
        String localIpAddress = scanner.nextLine();

        System.out.print("Enter the listening port for this process: ");
        int listeningPort = scanner.nextInt();

        LamportMutualExclusion lamportMutualExclusion = new LamportMutualExclusion(localIpAddress, listeningPort);

        System.out.print("Enter the number of peers(excluding this): ");
        int numberOfPeers = scanner.nextInt();

        if (numberOfPeers < 1) {
            System.err.print("Number of peers must be at least 1");
            return;
        }

        for (int i=0; i<numberOfPeers; i++) {
            System.out.println("Enter the ipAddress for peer " + (i + 1) + ": ");
            String backslash = scanner.nextLine();
            String ipAddress = scanner.nextLine();

            System.out.println("Enter the port for peer " + (i + 1) + ": ");
            int port = scanner.nextInt();

            PeerInformation peer = new PeerInformation(ipAddress, port);

            lamportMutualExclusion.addPeerInformation(peer);
        }

        lamportMutualExclusion.startListening();
        lamportMutualExclusion.startEventGeneration();
        lamportMutualExclusion.waitForThreads();
    }
}