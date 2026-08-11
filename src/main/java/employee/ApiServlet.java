package employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Api Servlet", description = "API services", urlPatterns = { "/api" })
public class ApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static EmployeeHibernateApi api;

    static {
        HibernateUtil.configure();
        try {
            api = new EmployeeHibernateApi();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    // Retrieving
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println("<h1>Hello, Super Manish!</h1>");

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if (idParam == null) {
            try {
                List<EmployeePojo> list = api.selectAll();
               String json = JsonUtil.print(list);
               out.write(json);
            } catch (SQLException e) {
                throw new ServletException("Error retrieving employees List", e);
            }
            return;

        }

        if (action != null && action.equals("delete")) {
            doDelete(request, response);
            return;
        }

        int id = Integer.valueOf(idParam);
        EmployeePojo p = null;
        try{
            p = api.select(id);
            String json = JsonUtil.print(p);
            response.getWriter().write(json);
        } catch (SQLException e) {
            throw new ServletException("Error retrieving Single employee", e);
        }

    }

    // creating
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EmployeePojo p;
        if (isJsonRequest(request)) {
            p = JsonUtil.print(readBody(request));
            if (p == null || p.getName() == null || p.getId() == 0 || p.getAge() == 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "name, and non-zero id and age are required");
                return;
            }
        } else {
            String name = request.getParameter("name");
            String age = request.getParameter("age");
            String id = request.getParameter("id");

            if (id == null || name == null || age == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "name, age and id are required");
                return;
            }

            p = new EmployeePojo();
            p.setId(Integer.parseInt(id));
            p.setName(name);
            p.setAge(Integer.parseInt(age));
        }

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
    private String readBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }

    private Map<String, String> parseFormBody(HttpServletRequest request) throws IOException {
        Map<String, String> params = new HashMap<>();
        for (String pair : readBody(request).split("&")) {
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

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("application/json");
    }

    // Map<String, String> (not EmployeePojo) so callers can tell "field absent"
    // from "field is 0/empty" — needed for doPut's partial-update semantics.
    private Map<String, String> parseJsonBody(HttpServletRequest request) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> raw;
        try {
            raw = mapper.readValue(readBody(request), new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new ServletException("Error parsing JSON body", e);
        }
        Map<String, String> body = new HashMap<>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            if (entry.getValue() != null) {
                body.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return body;
    }

    // updating
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        EmployeePojo p = JsonUtil.readJson(request);
        

        int id = Integer.valueOf(idStr);
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


        int id = Integer.valueOf(request.getParameter("id"));
        try{
            api.delete(id);
        } catch (SQLException e) {
            throw new ServletException("Error deleting employee", e);
        }


    }

}
