package game.login;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class ResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

    /**
     * 응답 본문을 처리하는 메서드
     *
     * @param response 응답 객체
     */
    public void handleResponseContent(HttpObject response) {
        HttpResponse httpResponse = (HttpResponse) response;
        HttpContent httpContent = (HttpContent) response;
        int statusCode = httpResponse.status().code();
        String content = httpContent.content().toString(CharsetUtil.UTF_8);
        System.out.println(content);
        processResponse(new responseItem(statusCode, content));

    }

    private record responseItem(int code, String response){
    }


    private void processResponse(responseItem response) {
        switch (response.code()) {
            case 200 -> SuccessLogic(response);
            case 301,302,304 -> RedirectLogic(response);
            case 400,401,403,404 -> BadClientLogic(response);
            case 500,503 -> InternalServerErrorLogic(response);
        }
    }

    private void InternalServerErrorLogic(responseItem response) {
        log.debug("Internal server error");
        //재시도 메커니즘

        //사용자에게 상태반환

    }

    private void BadClientLogic(responseItem response) {
        log.debug("Bad client request");
        
        
    }

    private void RedirectLogic(responseItem response) {
        log.debug("Redirected");
        
        
    }

    private void SuccessLogic(responseItem response) {
        if(response.response().contains("login_success")){
            loginSuccess();
        }
        if(response.response().contains("room_joined")){
            log.info("Room joined");
            joiningSuccess();

        }

    }

    private void joiningSuccess() {
        log.info("joining success");
        disposeRoomFrame();
        gameStart();

    }

    private void gameStart() {
    }

    private void disposeRoomFrame() {

    }

    private void createdRoom() {
        log.info("Created room");
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
        JOptionPane.showMessageDialog(null, "Login success");
        new ServerFrame();
    }

}
