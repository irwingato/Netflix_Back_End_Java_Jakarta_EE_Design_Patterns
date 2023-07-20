import java.io.PrintWriter;

public class ComedyServlet extends ComedyServletTemplate {

    @Override
    protected void processResponse(String responseBody, PrintWriter out) {
        out.println(responseBody);
    }
}
