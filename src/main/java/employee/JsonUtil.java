package employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

public class JsonUtil {

    public static EmployeePojo readJson(HttpServletRequest request) throws IOException, ServletException {
        BufferedReader reader = request.getReader();
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return print(body.toString());
    }

    public static String print(EmployeePojo p) throws ServletException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(p);
        } catch (JsonProcessingException e) {
            throw new ServletException("Error converting employee to JSON", e);
        }
    }

    public static String print(List<EmployeePojo> p) throws ServletException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(p);
        } catch (JsonProcessingException e) {
            throw new ServletException("Error converting object to JSON", e);
        }
    }

    public static EmployeePojo print(String json) throws ServletException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, EmployeePojo.class);
        } catch (JsonProcessingException e) {
            throw new ServletException("Error Json to Obj", e);
        }
    }
}
