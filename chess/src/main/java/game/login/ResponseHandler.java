package game.login;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class ResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

    public void handleResponseContent(HttpResponse httpResponse, HttpContent httpContent) {
        int statusCode = httpResponse.status().code();
        String content = httpContent.content().toString(CharsetUtil.UTF_8);
        processResponse(new ResponseItem(statusCode, content));
    }


    private record ResponseItem(int code, String response) {
    }

    private void processResponse(ResponseItem response) {
        switch (response.code()) {
            case 200 -> handleSuccess(response);
            case 301, 302, 304 -> handleRedirect(response);
            case 400, 401, 403, 404 -> handleClientError(response);
            case 500, 503 -> handleServerError(response);
            default -> log.warn("Unhandled response code: {}", response.code());
        }
    }

    private void handleServerError(ResponseItem response) {
        log.debug("Internal server error: {}", response.response());
        // 재시도 메커니즘
        // 사용자에게 상태 반환
    }

    private void handleClientError(ResponseItem response) {
        log.debug("Client error: {}", response.response());
        // 클라이언트 오류 처리 로직
    }

    private void handleRedirect(ResponseItem response) {
        log.debug("Redirected: {}", response.response());
        // 리다이렉트 처리 로직
    }

    private void handleSuccess(ResponseItem response) {
        String responseBody = response.response();
        if (responseBody.contains("login_success")) {
            loginSuccess();
        } else if (responseBody.contains("room_joined")) {
            log.info("Room joined");
            joiningSuccess();
        } else if (responseBody.contains("room_created")) {
            createdRoom();
        } else if (responseBody.contains("username_available")) {
            possibleUsername();
        } else if (responseBody.contains("username_invalid")) {
            invalidUsername();
        } else if (responseBody.contains("username_duplicate")) {
            duplicateUsername();
        } else if (responseBody.contains("register_failed")) {
            registerFail();
        } else if (responseBody.contains("register_success")) {
            registerSuccess();
        } else {
            log.warn("Unhandled success response: {}", responseBody);
        }
    }

    private void joiningSuccess() {
        log.info("Joining success");
        disposeRoomFrame();
        gameStart();
    }

    private void gameStart() {
        // 게임 시작 로직
    }

    private void disposeRoomFrame() {
        // 방 프레임 종료 로직
    }

    private void createdRoom() {
        log.info("Created room");
        // 방 생성 성공 로직
    }

    private void possibleUsername() {
        log.info("Possible username");
        // 유효한 사용자 이름 로직
    }

    private void invalidUsername() {
        log.info("Invalid username");
        // 유효하지 않은 사용자 이름 로직
    }

    private void duplicateUsername() {
        log.info("Duplicate username");
        // 중복된 사용자 이름 로직
    }

    private void registerFail() {
        log.info("Register failed");
        // 회원가입 실패 로직
    }

    private void registerSuccess() {
        log.info("Register success");
        // 회원가입 성공 로직
    }

    private void loginFail() {
        log.info("Login failed");
        // 로그인 실패 로직
    }

    private void loginSuccess() {
        log.info("Login success");
        JOptionPane.showMessageDialog(null, "Login success");
        new ServerFrame();
    }
}