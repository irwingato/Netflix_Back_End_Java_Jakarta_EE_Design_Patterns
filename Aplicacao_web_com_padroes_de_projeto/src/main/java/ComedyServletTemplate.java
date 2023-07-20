import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/ComedyServletTemplate"})
public abstract class ComedyServletTemplate extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String apiKey = "5eb3da233d676a59a9f8ed314c9075b5";
            String comedyPath = "/discover/tv?api_key=" + apiKey + "&with_genres=35";

            HttpSession session = request.getSession();
            Integer idadeUsuario = (Integer) session.getAttribute("idade");
            if (idadeUsuario == null) {
                idadeUsuario = 0;
            }

            String certificationCountry = "br";
            String certificationFilter = getCertificationFilter(idadeUsuario, certificationCountry);
            comedyPath += certificationFilter;

            String apiUrl = "https://api.themoviedb.org/3" + comedyPath;

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

            processResponse(responseBody.toString(), out);
        } catch (IOException e) {
            out.println("Erro ao obter dados das comédias: " + e.getMessage());
        }
    }

    protected abstract void processResponse(String responseBody, PrintWriter out);

    private String getCertificationFilter(int idadeUsuario, String certificationCountry) {
        String filter = "";

        if (idadeUsuario < 10) {
            filter = "&certification.lte=10";
        } else if (idadeUsuario < 12) {
            filter = "&certification.lte=12";
        } else if (idadeUsuario < 14) {
            filter = "&certification.lte=14";
        } else if (idadeUsuario < 16) {
            filter = "&certification.lte=16";
        } else if (idadeUsuario < 18) {
            filter = "&certification.lte=18";
        } else {
            filter = "&certification.lte=R";
        }

        filter += "&certification_country=" + certificationCountry;

        return filter;
    }
}
