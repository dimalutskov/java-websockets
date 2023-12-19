package com.dlapp.spaceships.game.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class BaseHttpServlet extends HttpServlet {

    protected static final String CONTENT_TYPE_JSON = "application/json";
    protected static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    protected static final ObjectMapper sObjectMapper = new ObjectMapper();

    protected String readRequestBody(HttpServletRequest request) {
        StringBuffer buffer = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                buffer.append(line);
        } catch (Exception e) { /*report an error*/ }
        return buffer.toString();
    }

    protected void sendResponse(HttpServletResponse resp, String contentType, String body) throws IOException {
        resp.setContentType(contentType);
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(body);
        out.flush();
    }

    protected void sendError(HttpServletResponse response, String msg) throws IOException {
        ObjectNode result = sObjectMapper.createObjectNode();
        ObjectNode error = sObjectMapper.createObjectNode();
        error.put("message", msg);
        result.put("error", error);
        sendResponse(response, CONTENT_TYPE_JSON, result.toString());
    }

}
