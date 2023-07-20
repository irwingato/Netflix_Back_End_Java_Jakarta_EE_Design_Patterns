
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelos.Usuario;

public class TrendingServletChainOfResponsability extends HttpServlet {

    private AgeFilterChain filterChain;

    @Override
    public void init() throws ServletException {
        // Configura a cadeia de filtros
        filterChain = new AgeFilterChain();
        filterChain.addFilter(new AgeFilter(10, "&certification.lte=10"));
        filterChain.addFilter(new AgeFilter(12, "&certification.lte=12"));
        filterChain.addFilter(new AgeFilter(14, "&certification.lte=14"));
        filterChain.addFilter(new AgeFilter(16, "&certification.lte=16"));
        filterChain.addFilter(new AgeFilter(18, "&certification.lte=18"));
        filterChain.addFilter(new AgeFilter(Integer.MAX_VALUE, "&certification.lte=R"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String apiKey = "5eb3da233d676a59a9f8ed314c9075b5";
            String trendingPath = "/trending/all/week?api_key=" + apiKey + "&language=pt-BR";

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
                String certificationFilter = filterChain.applyFilters(idadeUsuario, certificationCountry);
                trendingPath += certificationFilter;

                String apiUrl = "https://api.themoviedb.org/3" + trendingPath;

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
            out.println("Erro ao obter dados de tendências: " + e.getMessage());
        }
    }

    private static class AgeFilter {

        private final int ageLimit;
        private final String certificationFilter;

        public AgeFilter(int ageLimit, String certificationFilter) {
            this.ageLimit = ageLimit;
            this.certificationFilter = certificationFilter;
        }

        public boolean canHandle(int idadeUsuario) {
            return idadeUsuario < ageLimit;
        }

        public String getCertificationFilter() {
            return certificationFilter;
        }
    }

    private static class AgeFilterChain {

        private final List<AgeFilter> filters;

        public AgeFilterChain() {
            filters = new ArrayList<>();
        }

        public void addFilter(AgeFilter filter) {
            filters.add(filter);
        }

        public String applyFilters(int idadeUsuario, String certificationCountry) {
            StringBuilder filterBuilder = new StringBuilder();
            for (AgeFilter filter : filters) {
                if (filter.canHandle(idadeUsuario)) {
                    filterBuilder.append(filter.getCertificationFilter());
                }
            }
            filterBuilder.append("&certification_country=").append(certificationCountry);
            return filterBuilder.toString();
        }
    }
}
