package com.dlapp.spaceships.game.http;

import com.dlapp.spaceships.ServerApp;
import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.desc.AliveEntityDesc;
import com.dlapp.spaceships.game.desc.SkillDesc;
import com.dlapp.spaceships.game.entity.WorldAliveEntity;
import com.dlapp.spaceships.game.entity.WorldEntity;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

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
        try {
            JsonNode jsonNode = sObjectMapper.readTree(readRequestBody(req));
            String path = readEntityIdPath(req);
            if (path == null) {
                // Create entity
                String id = ENTITY_PREFIX + sEntityIndex++;

                AliveEntityDesc desc = new AliveEntityDesc(GameConstants.ENTITY_TYPE_SPACESHIP,
                        jsonNode.get("size").asInt(), jsonNode.get("health").asInt(), jsonNode.get("energy").asInt(),
                        Arrays.asList(SkillDesc.SKILL_ENERGY_RECOVER, SkillDesc.SKILL_SHOT, SkillDesc.SKILL_ACCELERATION));

                WorldEntity entity = new WorldAliveEntity(ServerApp.instance().getRoom(), id, desc,
                        jsonNode.get("x").asInt(),
                        jsonNode.get("y").asInt(),
                        jsonNode.get("angle").asInt());
                ServerApp.instance().getRoom().addEntity(entity, System.currentTimeMillis());
                sendResponse(resp, CONTENT_TYPE_JSON, entity.toJson().toString());
            } else {
                // Update entity
                WorldEntity entity = ServerApp.instance().getRoom().getEntity(path);
                if (entity == null) {
                    sendError(resp, "Entity not found");
                } else {
                    int size = jsonNode.has("size") ? jsonNode.get("size").asInt() : entity.getState().getSize();
                    int x = jsonNode.has("x") ? jsonNode.get("x").asInt() : entity.getState().getX();
                    int y = jsonNode.has("y") ? jsonNode.get("y").asInt() : entity.getState().getY();
                    int angle = jsonNode.has("angle") ? jsonNode.get("angle").asInt() : entity.getState().getAngle();
                    entity.updateSize(size);
                    entity.update(System.currentTimeMillis(), x, y, angle, 0);
                    if (entity instanceof WorldAliveEntity) {
                        WorldAliveEntity.State state = ((WorldAliveEntity) entity).getState();
                        int health = jsonNode.has("health") ? jsonNode.get("health").asInt() : (int) state.getHealth();
                        int energy = jsonNode.has("energy") ? jsonNode.get("energy").asInt() : (int) state.getEnergy();
                        state.update(health, energy);
                    }
                    sendResponse(resp, CONTENT_TYPE_JSON, entity.toJson().toString());
                }
            }
        } catch (Exception e) {
            sendError(resp, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entityId = readEntityIdPath(req);
        WorldEntity entity = ServerApp.instance().getRoom().getEntity(entityId);
        if (entity == null) {
            sendError(resp, "Entity not found");
        } else {
            entity.destroy();
            sendResponse(resp, CONTENT_TYPE_JSON, entity.toJson().toString());
        }
    }

    private static String readEntityIdPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? null : pathInfo.substring(1).split("/")[0];
    }
}
