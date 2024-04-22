package model;

public class PriorityQueueElement {
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
    public String toString() {
        return "PriorityQueueElement{" +
                "timeStamp=" + timeStamp +
                ", peerInformation=" + peerInformation +
                '}';
    }
}
