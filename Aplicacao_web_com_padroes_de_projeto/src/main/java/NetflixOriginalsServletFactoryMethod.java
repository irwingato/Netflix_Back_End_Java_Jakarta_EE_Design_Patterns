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

public class NetflixOriginalsServletFactoryMethod extends HttpServlet {
    private static final String API_KEY = "5eb3da233d676a59a9f8ed314c9075b5";
    private static final String CERTIFICATION_COUNTRY = "br";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String originalsPath = "/discover/tv?api_key=" + API_KEY + "&with_networks=213";
            String apiUrl = "https://api.themoviedb.org/3" + originalsPath;

            HttpSession session = request.getSession();
            Integer idadeUsuario = (Integer) session.getAttribute("idade");
            if (idadeUsuario == null) {
                idadeUsuario = 0;
            }

            String idadeParam = request.getParameter("idade");
            if (idadeParam != null) {
                idadeUsuario = Integer.parseInt(idadeParam);
                session.setAttribute("idade", idadeUsuario);
            }

            CertificationFilterFactory filterFactory = new CertificationFilterFactory();
            CertificationFilter certificationFilter = filterFactory.createCertificationFilter(idadeUsuario, CERTIFICATION_COUNTRY);
            originalsPath += certificationFilter.getCertificationFilter();

            apiUrl = "https://api.themoviedb.org/3" + originalsPath;

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
        } catch (IOException e) {
            out.println("Erro ao obter dados dos originais Netflix: " + e.getMessage());
        }
    }
}

interface CertificationFilter {
    String getCertificationFilter();
}

class CertificationFilterFactory {
    public CertificationFilter createCertificationFilter(int idadeUsuario, String certificationCountry) {
        if (idadeUsuario < 10) {
            return new CertificationFilter10(certificationCountry);
        } else if (idadeUsuario < 12) {
            return new CertificationFilter12(certificationCountry);
        } else if (idadeUsuario < 14) {
            return new CertificationFilter14(certificationCountry);
        } else if (idadeUsuario < 16) {
            return new CertificationFilter16(certificationCountry);
        } else if (idadeUsuario < 18) {
            return new CertificationFilter18(certificationCountry);
        } else {
            return new CertificationFilterR(certificationCountry);
        }
    }
}

class CertificationFilter10 implements CertificationFilter {
    private final String certificationCountry;

    public CertificationFilter10(String certificationCountry) {
        this.certificationCountry = certificationCountry;
    }

    @Override
    public String getCertificationFilter() {
        return "&certification.lte=10&certification_country=" + certificationCountry;
    }
}

class CertificationFilter12 implements CertificationFilter {
    private final String certificationCountry;

    public CertificationFilter12(String certificationCountry) {
        this.certificationCountry = certificationCountry;
    }

    @Override
    public String getCertificationFilter() {
        return "&certification.lte=12&certification_country=" + certificationCountry;
    }
}

class CertificationFilter14 implements CertificationFilter {
    private final String certificationCountry;

    public CertificationFilter14(String certificationCountry) {
        this.certificationCountry = certificationCountry;
    }
    @Override
    public String getCertificationFilter() {
        return "&certification.lte=14&certification_country=" + certificationCountry;
    }
}

class CertificationFilter16 implements CertificationFilter {
    private final String certificationCountry;

    public CertificationFilter16(String certificationCountry) {
        this.certificationCountry = certificationCountry;
    }

    @Override
    public String getCertificationFilter() {
        return "&certification.lte=16&certification_country=" + certificationCountry;
    }
}

class CertificationFilter18 implements CertificationFilter {
    private final String certificationCountry;

    public CertificationFilter18(String certificationCountry) {
        this.certificationCountry = certificationCountry;
    }

    @Override
    public String getCertificationFilter() {
        return "&certification.lte=18&certification_country=" + certificationCountry;
    }
}

class CertificationFilterR implements CertificationFilter {
    private final String certificationCountry;

    public CertificationFilterR(String certificationCountry) {
        this.certificationCountry = certificationCountry;
    }

    @Override
    public String getCertificationFilter() {
        return "&certification.lte=R&certification_country=" + certificationCountry;
    }
}