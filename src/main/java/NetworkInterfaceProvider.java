import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class NetworkInterfaceProvider {
    public static void main(String[] args) {

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            Collections.list(networkInterfaces).forEach(networkInterface -> {
                System.out.println(String.format("name: %s ->\n\tdesc: %s\n\taddr: %s",
                        networkInterface.getName(), networkInterface.getDisplayName(),
                        networkInterface.inetAddresses().findFirst().orElse(null)));
            });

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }
}
