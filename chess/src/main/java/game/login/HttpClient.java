package game.login;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;


public class HttpClient extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static ResponseHandler responseHandler = new ResponseHandler();

    private static String session_id;

    public HttpClient(ResponseHandler responseHandler){
        HttpClient.responseHandler = responseHandler;
    }

    public static void sendJoinRoomRequest(String selectedRoom) {
        try{
            sendRequest("http://localhost:8000/api/room/join/", selectedRoom, HttpMethod.POST);
        }catch (Exception e){
            log.error("Failed to send join room request", e);
        }
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        HttpClient.session_id = session_id;
    }


    public static void sendRoomListRequest() {
        try{
            sendRequest("http://localhost:8000/api/room/list/", "", HttpMethod.GET);
        } catch (URISyntaxException | InterruptedException e) {
            log.error("Failed to send room list request", e);
        }
    }

    public static void sendIdDuplicateCheckRequest(String message) {
        try {
            sendRequest("http://localhost:8000/api/check/id/",message,HttpMethod.GET );
        } catch (URISyntaxException | InterruptedException e) {
            log.error("Failed to send id duplicate check request", e);
        }
    }

    public static void sendCreateRoomRequest(String message) {
        try {
            sendRequest("http://localhost:8000/api/room/create/", message, HttpMethod.POST);
        } catch (URISyntaxException | InterruptedException e) {
            log.error("Failed to send room create request", e);
        }
    }

    public static void sendLoginRequest(String message) throws URISyntaxException {
        try {
            sendRequest("http://localhost:8000/api/user/login/", message, HttpMethod.POST);
        } catch (URISyntaxException e) {
            log.error("Failed to send login request", e);
            new LoginFrame();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendRegisterRequest(String message){
        try {
            sendRequest("http://localhost:8000/api/user/register/", message,HttpMethod.POST );
        } catch (URISyntaxException | InterruptedException e) {
            log.error("Failed to send register request", e);
        }
    }

    public static void sendRequest(String url, String message, HttpMethod method) throws URISyntaxException, InterruptedException {
        URI uri = new URI(url);
        String host = uri.getHost();
        int port = uri.getPort();
        Bootstrap b = configureBootStrap();
        ChannelFuture connectFuture = b.connect(host, port);
        connectFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                Channel ch = future.channel();
                DefaultFullHttpRequest request = getDefaultFullHttpRequest(message, method, uri,host);
                if (session_id != null) {
                    request.headers().set("Cookie", "sessionid=" + session_id);
                }
                ChannelFuture channelFuture = ch.writeAndFlush(request);
                channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                    if (channelFuture1.isSuccess()) {
                        System.out.println("Request sent");
                    } else {
                        Throwable cause = channelFuture1.cause();
                        log.error("Failed to send request", cause);
                    }
                });
                System.out.println("Connected to server");
            } else {
                Throwable cause = future.cause();
                log.error("Failed to connect to server", cause);
            }
        });
    }

    private static void setRequestHeaders(DefaultFullHttpRequest request, String host) {
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
    }

    private static DefaultFullHttpRequest getDefaultFullHttpRequest(String message, HttpMethod method, URI uri, String host) {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, method, uri.getRawPath(),
                Unpooled.wrappedBuffer(message.getBytes()));
        setRequestHeaders(request, host);
        return request;
    }

    private static Bootstrap configureBootStrap() {
        return new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new HttpClientCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                ch.pipeline().addLast(new HttpClient(responseHandler));
            }
        });
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse response) {
            log.info("HttpResponse received");
            handleHttpResponse((HttpResponse)msg);
            System.out.println("Response: " + response.status());
        }
        if (msg instanceof HttpContent content) {
            log.info("HttpContent received");
            handleHttpContent(ctx, msg, content);
        }
    }

    private static void handleHttpContent(ChannelHandlerContext ctx, HttpObject msg, HttpContent content) {
        content.content();
        System.out.println("Content: " + content.content().toString(CharsetUtil.UTF_8));
        if(responseHandler != null){
            System.out.println("Response handler is not null");
            responseHandler.handleResponseContent(msg);
        }else{
            log.info("Response handler is null");
        }

        if (content instanceof LastHttpContent) {
            ctx.close(); // 연결 종료
        }
    }

    private void handleHttpResponse(HttpResponse response) {
        String sessionHeader = response.headers().get("Set-Cookie");
        if(sessionHeader != null){
            String sessionId = extractionSessionId(sessionHeader);
            setSession_id(sessionId);
        }
    }

    private String extractionSessionId(String sessionHeader) {
        String[] session = sessionHeader.split(";");
        String[] session_id = session[0].split("=");
        return session_id[1];
    }

}

