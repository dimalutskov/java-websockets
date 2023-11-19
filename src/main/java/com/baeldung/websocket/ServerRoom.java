package com.baeldung.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServerRoom {

    private final List<RoomUser> users = new ArrayList();

    private Timer timer = new Timer();

    void connectUser(RoomUser user) {
        users.add(user);
        if (users.size() == 1) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    proceed();
                }
            }, 0, 1000);
        }
    }

    void disconnectUser(RoomUser user) {
        users.remove(user);
        if (users.size() == 0 && timer != null) {
            timer.cancel();
        }
    }

    private void proceed() {
        broadcast("tick");
    }

    private void broadcast(String message) {
        for (RoomUser user : users) {
            user.send(message);
        }
    }

}
