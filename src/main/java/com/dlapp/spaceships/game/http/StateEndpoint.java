package com.dlapp.spaceships.game.http;

import com.dlapp.spaceships.ServerApp;
import com.dlapp.spaceships.game.WorldState;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/state")
public class StateEndpoint extends HttpServlet {

    private static final String CONTENT_TYPE_JSON = "application/json";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WorldState state = ServerApp.instance().getRoom().getLastState();
        if (state == null) {
            send(resp, CONTENT_TYPE_JSON, "{}");
        } else {
            send(resp, CONTENT_TYPE_JSON, state.toJson().toString());
        }
    }

    protected void send(HttpServletResponse resp, String contentType, String body) throws IOException {
        resp.setContentType(contentType);
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(body);
        out.flush();
    }

}
