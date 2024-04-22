package model;

public record PriorityQueueElement(int timeStamp,
                                   PeerInformation peerInformation) implements Comparable<PriorityQueueElement> {

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
