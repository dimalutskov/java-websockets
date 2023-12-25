package com.dlapp.spaceships.game.http;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/state")
public class StateEndpoint extends BaseHttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        WorldState state = ServerApp.instance().getRoom().getLastState();
//        if (state == null) {
            sendResponse(resp, CONTENT_TYPE_JSON, "{}");
//        } else {
//            sendResponse(resp, CONTENT_TYPE_JSON, state.toJson().toString());
//        }
    }

}
