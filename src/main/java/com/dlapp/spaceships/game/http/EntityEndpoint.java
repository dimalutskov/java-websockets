package com.dlapp.spaceships.game.http;

import com.dlapp.spaceships.ServerApp;
import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.entity.WorldEntity;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/entity/*"})
public class EntityEndpoint extends BaseHttpServlet {

    private static final String ENTITY_PREFIX = "test_entity_";

    private static int sEntityIndex = 0;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entityId = readEntityIdPath(req);
        WorldEntity entity = ServerApp.instance().getRoom().getEntity(entityId);
        if (entity == null) {
            sendError(resp, "Entity not found");
        } else {
            sendResponse(resp, CONTENT_TYPE_JSON, entity.toJson().toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            JsonNode jsonNode = sObjectMapper.readTree(readRequestBody(req));
//            String id = ENTITY_PREFIX + sEntityIndex++;
//            WorldEntity entity = new WorldEntity.Simple(ServerApp.instance().getRoom(), id, GameConstants.ENTITY_TYPE_SPACESHIP,
//                    jsonNode.get("size").asInt(),
//                    jsonNode.get("x").asInt(),
//                    jsonNode.get("y").asInt(),
//                    jsonNode.get("angle").asInt());
//            ServerApp.instance().getRoom().addEntity(entity);
//            sendResponse(resp, CONTENT_TYPE_JSON, entity.toJson().toString());
//        } catch (Exception e) {
//            sendError(resp, e.getMessage());
//        }
    }

    private static String readEntityIdPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? null : pathInfo.substring(1).split("/")[0];
    }
}
