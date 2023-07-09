/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Usuario
 */
@WebServlet(urlPatterns = {"/RomancesProxyServlet"})
public class RomancesProxyServlet extends HttpServlet {

    private RomancesServlet romancesServlet;

    @Override
    public void init() throws ServletException {
        super.init();
        romancesServlet = new RomancesServlet();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Additional functionality before calling the actual servlet
        // For example, you can modify the request or perform some pre-processing

        // Call the actual servlet
        romancesServlet.doGet(request, response);

        // Additional functionality after the servlet call
        // For example, you can modify the response or perform some post-processing
    }
}
