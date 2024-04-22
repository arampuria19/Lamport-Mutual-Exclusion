package model;

public class PriorityQueueElement implements Comparable<PriorityQueueElement > {
    private final int timeStamp;
    private final PeerInformation peerInformation;

    public PriorityQueueElement(int timeStamp, PeerInformation peerInformation) {
        this.timeStamp = timeStamp;
        this.peerInformation = peerInformation;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public PeerInformation getPeerInformation() {
        return peerInformation;
    }

    @Override
    public int compareTo(PriorityQueueElement other) {
        // Compare this element's timestamp with the other element's timestamp
        return Integer.compare(this.timeStamp, other.timeStamp);
    }

    @Override
    public String toString() {
        return "PriorityQueueElement{" +
                "timeStamp=" + timeStamp +
                ", peerInformation=" + peerInformation +
                '}';
    }
}
