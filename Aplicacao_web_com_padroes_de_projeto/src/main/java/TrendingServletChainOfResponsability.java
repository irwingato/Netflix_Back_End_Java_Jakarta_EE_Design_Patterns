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

            // Obtenha a sessão atual ou crie uma nova se ainda não existir
            HttpSession session = request.getSession();

            // Obtenha a idade do usuário da sessão
            Integer idadeUsuario = (Integer) session.getAttribute("idade");
            if (idadeUsuario == null) {
                // Se a idade não estiver definida na sessão, defina um valor padrão
                idadeUsuario = 0;
            }

            // Atualize a idade do usuário com base no parâmetro da solicitação, se fornecido
            String idadeParam = request.getParameter("idade");
            if (idadeParam != null) {
                idadeUsuario = Integer.parseInt(idadeParam);
                // Atualize a idade do usuário na sessão
                session.setAttribute("idade", idadeUsuario);
            }

            // Aplica o filtro de classificação de idade com base na idade do usuário
            String certificationCountry = "br";
            String certificationFilter = filterChain.applyFilters(idadeUsuario, certificationCountry);
            trendingPath += certificationFilter;

            String apiUrl = "https://api.themoviedb.org/3" + trendingPath;

            // Faz uma requisição GET para a API
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            StringBuilder responseBody;
            try ( // Lê a resposta da API
                    Scanner scanner = new Scanner(url.openStream())) {
                responseBody = new StringBuilder();
                while (scanner.hasNext()) {
                    responseBody.append(scanner.nextLine());
                }
                // Fecha as conexões
            }
            conn.disconnect();

            // Envia a resposta como saída do servlet
            out.println(responseBody.toString());
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
