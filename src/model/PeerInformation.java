package model;

import java.util.Objects;

public record PeerInformation(String ipAddress, int port) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerInformation that = (PeerInformation) o;
        return port == that.port && ipAddress.equals(that.ipAddress);
    }

    @Override
    public String toString() {
        return "{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                '}';
    }
}
