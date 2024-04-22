package model;

public record PeerInformation(String ipAddress, int port) {

    @Override
    public String toString() {
        return "{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                '}';
    }
}
