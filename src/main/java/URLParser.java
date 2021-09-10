import java.net.MalformedURLException;
import java.net.URL;

public class URLParser {

    public static void main(String[] args) throws MalformedURLException {

        URL url = new URL("https://en.wikipedia.org/wiki/URL#Citations");
        System.out.println("File: " + url.getFile());
        System.out.println("Protocol: " + url.getProtocol());
        System.out.println("Host: " + url.getHost());
        System.out.println("User Info: " + url.getUserInfo());
        System.out.println("Port: " + url.getPort());
        System.out.println("Authority: " + url.getAuthority());
        System.out.println("Path: " + url.getPath());
        System.out.println("Query: " + url.getQuery());
        System.out.println("Ref: " + url.getRef());

    }

}
