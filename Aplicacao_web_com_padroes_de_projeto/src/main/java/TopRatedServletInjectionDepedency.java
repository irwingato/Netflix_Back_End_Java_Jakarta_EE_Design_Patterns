import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TopRatedServletInjectionDepedency extends HttpServlet {

    private ApiService apiService;

    public TopRatedServletInjectionDepedency(ApiService apiService) {
        this.apiService = apiService;
    }

    public TopRatedServletInjectionDepedency() {
        this(new ApiService());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String apiKey = "5eb3da233d676a59a9f8ed314c9075b5";
            String topRatedPath = "/movie/top_rated?api_key=" + apiKey + "&language=pt-BR";

            // Obtenha a sessão atual ou crie uma nova se ainda não existir
            HttpSession session = request.getSession();

            // Obtenha a idade do usuário da sessão
            Integer idadeUsuario = (Integer) session.getAttribute("idade");
            if (idadeUsuario == null) {
                // Se a idade não estiver definida na sessão, defina um valor padrão
                idadeUsuario = 0;
            }

            String idadeParam = request.getParameter("idade");
            if (idadeParam != null) {
                idadeUsuario = Integer.parseInt(idadeParam);
                // Atualize a idade do usuário na sessão
                session.setAttribute("idade", idadeUsuario);
            }

            // Aplica o filtro de classificação de idade com base na idade do usuário
            String certificationCountry = "br";
            String certificationFilter = getCertificationFilter(idadeUsuario, certificationCountry);
            topRatedPath += certificationFilter;

            String apiUrl = "https://api.themoviedb.org/3" + topRatedPath;

            // Faz uma requisição GET para a API usando o ApiService
            String responseBody = apiService.get(apiUrl);

            // Envia a resposta como saída do servlet
            out.println(responseBody);
        } catch (IOException e) {
            out.println("Erro ao obter dados dos filmes mais populares: " + e.getMessage());
        }
    }

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
