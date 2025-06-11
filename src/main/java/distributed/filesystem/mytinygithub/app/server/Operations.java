package distributed.filesystem.mytinygithub.app.server;

public enum Operations {
    POST("POST"),
    GET("GET"),
    LIST("LIST"),
    CONNECT("CONNECT");

    final String action;

    Operations(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
