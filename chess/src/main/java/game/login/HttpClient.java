package game.login;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpClient extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static ResponseHandler responseHandler;
    @Getter
    private static String sessionId;

    public HttpClient(ResponseHandler responseHandler) {
        HttpClient.responseHandler = responseHandler;
    }

    public static void sendJoinRoomRequest(String selectedRoom) {
        sendRequest("http://localhost:8000/api/room/join/", selectedRoom, HttpMethod.POST);
    }


    public void setSessionId(String sessionId) {
        HttpClient.sessionId = sessionId;
    }

    public static void sendRoomListRequest() {
        sendRequest("http://localhost:8000/api/room/list/", "", HttpMethod.GET);
    }

    public static void sendIdDuplicateCheckRequest(String message) {
        sendRequest("http://localhost:8000/api/check/id/", message, HttpMethod.GET);
    }

    public static void sendCreateRoomRequest(String message) {
        sendRequest("http://localhost:8000/api/room/create/", message, HttpMethod.POST);
    }

    public static void sendLoginRequest(String message) {
        sendRequest("http://localhost:8000/api/user/login/", message, HttpMethod.POST);
    }

    public static void sendRegisterRequest(String message) {
        sendRequest("http://localhost:8000/api/user/register/", message, HttpMethod.POST);
    }

    private static void sendRequest(String url, String message, HttpMethod method) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 80 : uri.getPort();
            Bootstrap b = configureBootstrap();
            ChannelFuture connectFuture = b.connect(host, port);
            connectFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    Channel ch = future.channel();
                    DefaultFullHttpRequest request = createHttpRequest(message, method, uri, host);
                    if (sessionId != null) {
                        request.headers().set("Cookie", "sessionid=" + sessionId);
                    }
                    ch.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture -> {
                        if (channelFuture.isSuccess()) {
                            log.info("Request sent successfully");
                        } else {
                            log.error("Failed to send request", channelFuture.cause());
                        }
                    });
                    log.info("Connected to server");
                } else {
                    log.error("Failed to connect to server", future.cause());
                }
            });
        } catch (URISyntaxException e) {
            log.error("Failed to send request", e);
        }
    }

    private static DefaultFullHttpRequest createHttpRequest(String message, HttpMethod method, URI uri, String host) {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, method, uri.getRawPath(),
                Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8)));
        setRequestHeaders(request, host);
        return request;
    }

    private static void setRequestHeaders(DefaultFullHttpRequest request, String host) {
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
    }

    private static Bootstrap configureBootstrap() {
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
            log.info("HttpResponse received: {}", response.status());
            ctx.channel().attr(AttributeKey.valueOf("httpResponse")).set(response);
        }
        if (msg instanceof HttpContent content) {
            log.info("HttpContent received");
            HttpResponse response = (HttpResponse) ctx.channel().attr(AttributeKey.valueOf("httpResponse")).get();
            handleHttpContent(ctx, response, content);
        }
    }

    private static void handleHttpContent(ChannelHandlerContext ctx, HttpResponse response, HttpContent content) {
        String contentStr = content.content().toString(CharsetUtil.UTF_8);
        log.info("Content: {}", contentStr);
        if (responseHandler != null) {
            responseHandler.handleResponseContent(response, content);
        } else {
            log.warn("Response handler is null");
        }

        if (content instanceof LastHttpContent) {
            ctx.close();
        }
    }

    private void handleHttpResponse(HttpResponse response) {
        String sessionHeader = response.headers().get(HttpHeaderNames.SET_COOKIE);
        if (sessionHeader != null) {
             sessionId = extractSessionId(sessionHeader);
            setSessionId(sessionId);
        }
    }

    private String extractSessionId(String sessionHeader) {
        String[] session = sessionHeader.split(";");
        String[] sessionIdPair = session[0].split("=");
        return sessionIdPair.length > 1 ? sessionIdPair[1] : null;
    }

    public static void shutdown() {
        workerGroup.shutdownGracefully();
    }
}