import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelos.Usuario;

interface AgeFilterStrategy {
    String applyFilter(int idadeUsuario, String certificationCountry);
}

class CertificationFilterStrategy implements AgeFilterStrategy {
    @Override
    public String applyFilter(int idadeUsuario, String certificationCountry) {
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

class OriginalsPathStrategy {
    public String getOriginalsPath(String apiKey) {
        return "/discover/tv?api_key=" + apiKey + "&with_networks=213";
    }
}

public class DocumentariesServletStrategy extends HttpServlet {
    private AgeFilterStrategy ageFilterStrategy;
    private OriginalsPathStrategy originalsPathStrategy;

    @Override
    public void init() throws ServletException {
        ageFilterStrategy = new CertificationFilterStrategy();
        originalsPathStrategy = new OriginalsPathStrategy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String apiKey = "5eb3da233d676a59a9f8ed314c9075b5";
            String originalsPath = originalsPathStrategy.getOriginalsPath(apiKey);

            HttpSession session = request.getSession();
            String sessionID = (String) session.getAttribute("sessionID");
            if (sessionID == null) {
                sessionID = "";
            }

            String sessionIDParam = request.getParameter("sessionID");
            if (sessionIDParam != null) {
                sessionID = sessionIDParam;
                session.setAttribute("sessionID", sessionID);
            }

            HttpSession userSession = (HttpSession) getServletContext().getAttribute(sessionID);
            if (userSession != null && userSession.getAttribute("usuario") != null) {
                Usuario usuario = (Usuario) userSession.getAttribute("usuario");
                int idadeUsuario = usuario.getIdade();
                String certificationCountry = "br";
                String certificationFilter = ageFilterStrategy.applyFilter(idadeUsuario, certificationCountry);
                originalsPath += certificationFilter;

                String apiUrl = "https://api.themoviedb.org/3" + originalsPath;

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

                out.println(responseBody.toString());
            } else {
                out.println("Usuário não autenticado.");
            }
        } catch (IOException e) {
            out.println("Erro ao obter dados dos originais Netflix: " + e.getMessage());
        }
    }
}
