package distributed.filesystem.mytinygithub.app.server;

import java.util.Optional;

public enum HttpStatusCode {
    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private final int code;
    HttpStatusCode(int code) {
        this.code = code;
    }

    public static Optional<String> getMessage(int code) {
        for (HttpStatusCode httpStatusCode : HttpStatusCode.values()) {
            if (httpStatusCode.code == code) {
                return Optional.of(httpStatusCode.name());
            }
        }

        return Optional.empty();
    }
}
