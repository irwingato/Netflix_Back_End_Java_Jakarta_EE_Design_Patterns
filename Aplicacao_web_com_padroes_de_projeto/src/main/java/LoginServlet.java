import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelos.Usuario;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Converte uma string em um objeto no formato indicado
        Gson gson = new Gson();
        Usuario user = gson.fromJson(request.getReader(), Usuario.class);

        System.out.println("email: " + user.getEmail());
        System.out.println("senha: " + user.getSenha());
        System.out.println("idade: " + user.getIdade());      

        // Verifica as credenciais
        if (isValidCredentials(user.getEmail(), user.getSenha())) {
            // Cria uma sessão para o usuário
            
            user.setSessionID(request.getSession().getId());
            
            // Obtém a idade fornecida pelo usuário a partir do formulário
            int idadeUsuario = user.getIdade();
            
            // Armazena a idade na sessão
            HttpSession session = request.getSession();
            session.setAttribute("idadeUsuario", idadeUsuario);
            
            String json = gson.toJson(user);
            
            response.getWriter().println(json);
        } else {
            // Exibe uma mensagem de erro
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Define o status de erro 401 Unauthorized
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Login Failed. Invalid email or password.</h2>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Configura os cabeçalhos CORS para a solicitação OPTIONS
        // response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        // response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        // response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        // response.setHeader("Access-Control-Max-Age", "86400"); // Define o tempo máximo de cache para 24 horas
    }

    private boolean isValidCredentials(String email, String senha) {
        // Verifica se o usuário e senha são válidos
        // Aqui você pode fazer a validação com base em um banco de dados, arquivo de configuração, etc.
        return email != null && senha != null && email.equals("admin@example") && senha.equals("admin123");
    }
}
