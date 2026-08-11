package employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "HelloWorld", description = "Hello World Servlet", urlPatterns = { "/hello" })

public class HelloWorldServlet extends HttpServlet {

    // This is a placeholder for the HelloWorldServlet class.
    // You can implement servlet methods here as needed.
    private static final long serialVersionUID = 1L;
    private static employee.EmployeeHibernateApi api;

    static{
        HibernateUtil.configure();
        try {
            api = new employee.EmployeeHibernateApi();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // public HelloWorldServlet() throws ClassNotFoundException, IOException, Exception {
    //     api = new employee.EmployeeHibernateApi();
    // }

    @Override
    // Retrieving
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h1>Hello, Super Manish!</h1>");

        String action = request.getParameter("action");
        if (action != null && action.equals("delete")) {
            doDelete(request, response);
            return;
        }

        int id = Integer.valueOf(request.getParameter("id"));
        EmployeePojo p = null;
        try{
            p = api.select(id);
        } catch (SQLException e) {
            throw new ServletException("Error retrieving employee", e);
        }
        out.println("<h2>Employee Details:</h2>");
        if (p != null) {
            out.println("<p>Name: " + p.getName() + "</p>");
            out.println("<br>");
            out.println("<p>Age: " + p.getAge() + "</p>");
            out.println("<br>");
            out.println("<p>ID: " + p.getId() + "</p>");
        } else {
            out.println("<p>Employee not found.</p>");
        }
    }

    // creating
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String age = request.getParameter("age");
        String id = request.getParameter("id");

        if (id == null || name == null || age == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "name, age and id are required");
            return;
        }

        EmployeePojo p = new EmployeePojo();
        p.setId(Integer.parseInt(id));
        p.setName(name);
        p.setAge(Integer.parseInt(age));

        try {
            api.insert(p);
        } catch (SQLException e) {
            throw new ServletException("Error inserting employee", e);
        }
        PrintWriter out = response.getWriter();
        out.println("<h2>Employee Created: " + p.getName() + " Successfully</h2>");

    }

    // Unlike POST, the servlet container does not parse x-www-form-urlencoded
    // PUT bodies into request parameters, so it must be read and decoded by hand.
    private Map<String, String> parseFormBody(HttpServletRequest request) throws IOException {
        Map<String, String> params = new HashMap<>();
        BufferedReader reader = request.getReader();
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        for (String pair : body.toString().split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name());
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()) : "";
            params.put(key, value);
        }
        return params;
    }

    // updating
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> body = parseFormBody(request);

        String idParam = request.getParameter("id");
        if (idParam == null) {
            idParam = body.get("id");
        }
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "id is required");
            return;
        }
        int id = Integer.parseInt(idParam);

        EmployeePojo p;
        try {
            p = api.select(id);
        } catch (SQLException e) {
            throw new ServletException("Error fetching employee", e);
        }
        if (p == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee not found: " + id);
            return;
        }

        String name = body.get("name");
        String age = body.get("age");
        if (name != null) {
            p.setName(name);
        }
        if (age != null) {
            p.setAge(Integer.parseInt(age));
        }

        try {
            api.update(id, p);
        } catch (SQLException e) {
            throw new ServletException("Error updating employee", e);
        }
    }

    // deleting
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        out.println("Deleting employee with ID: " + request.getParameter("id"));
        int id = Integer.valueOf(request.getParameter("id"));
        try{
            api.delete(id);
        } catch (SQLException e) {
            throw new ServletException("Error deleting employee", e);
        }

    }
}