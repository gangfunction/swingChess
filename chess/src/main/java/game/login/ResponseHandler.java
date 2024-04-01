package game.login;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

    /**
     * 응답 본문을 처리하는 메서드
     *
     * @param content ByteBuf 타입의 Http 응답 본문
     */
    public void handleResponseContent(ByteBuf content) {
        log.info("Response content: {}", content.toString());
        String response = content.toString(CharsetUtil.UTF_8);
        processResponse(response);
    }


    private void processResponse(String response) {
        log.info("Processing response: {}", response);
        switch (response) {
            case "login_success":
                loginSuccess();
                break;
            case "login_fail":
                loginFail();
                break;
            case "register_success":
                registerSuccess();
                break;
            case "register_fail":
                registerFail();
                break;
            case "duplicate_username":
                duplicateUsername();
                break;
            case "possible_username":
                possibleUsername();
                break;
            case "invalid_username":
                invalidUsername();
                break;
            default:
                log.info("Unknown response");

        }
    }

    private void possibleUsername() {
        log.info("Possible username");
    }

    private void invalidUsername() {
        log.info("Invalid username");
    }

    private void duplicateUsername() {
        log.info("Duplicate username");
    }

    private void registerFail() {
        log.info("Register failed");
    }

    private void registerSuccess() {
        log.info("Register success");
    }


    private void loginFail() {
        log.info("Login failed");
    }

    private void loginSuccess() {
        log.info("Login success");
        disposeLoginFrame();
        serverListOpened();

    }

    private void serverListOpened() {
        new ServerFrame();
    }

    private void disposeLoginFrame() {


    }
}
