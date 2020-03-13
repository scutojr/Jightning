package examples;

import clightning.plugin.*;
import com.fasterxml.jackson.databind.JsonNode;

public class SimplePlugin extends Plugin {

    protected Object initialize(JsonNode options, JsonNode configuration) {
        return null;
    }

    @Command
    public void cmd1(int p1, int p2, int p3) {
    }

    @Command
    public void cmd2(String pp1, String pp2) {
    }

    @Command
    public void cmd3(long ppp1) {
    }

    @Subscribe(NotificationTopic.channel_opened)
    public void receiveNotification(String pp1) {

    }

    @Hook(HookTopic.db_write)
    public void handleHook(JsonNode p) {
    }
}
