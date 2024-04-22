package model;

public class PeerInformation {
    private final String ipAddress;
    private final int port;

    public PeerInformation(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}
