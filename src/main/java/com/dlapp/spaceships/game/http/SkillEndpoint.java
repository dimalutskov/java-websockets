package com.dlapp.spaceships.game.http;

import com.dlapp.spaceships.ServerApp;
import com.dlapp.spaceships.game.GameConstants;
import com.dlapp.spaceships.game.desc.SkillDesc;
import com.dlapp.spaceships.game.object.GameEntity;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/skill"})
public class SkillEndpoint extends BaseHttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            JsonNode jsonNode = sObjectMapper.readTree(readRequestBody(req));
            GameEntity entity = (GameEntity) ServerApp.instance().getWorld().getEntity(jsonNode.get("id").asText());
            int skillId = jsonNode.get("skillId").asInt();
            if (GameConstants.SKILL_TYPE_SHOT == skillId) {
                entity.handleShotSkill(System.currentTimeMillis(), SkillDesc.SKILL_SHOT,
                        entity.getState().getX(), entity.getState().getY(), entity.getState().getAngle());
            }
        } catch (Exception e) {
            sendError(resp, e.getMessage());
        }
    }
}
