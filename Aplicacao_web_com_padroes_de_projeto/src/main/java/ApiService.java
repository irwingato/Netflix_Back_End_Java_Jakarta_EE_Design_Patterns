import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ApiService {

    public String get(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        StringBuilder responseBody;
        try (Scanner scanner = new Scanner(url.openStream())) {
            responseBody = new StringBuilder();
            while (scanner.hasNext()) {
                responseBody.append(scanner.nextLine());
            }
        }
        conn.disconnect();

        return responseBody.toString();
    }
}
