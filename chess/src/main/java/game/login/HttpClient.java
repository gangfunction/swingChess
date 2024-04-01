package game.login;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpClient extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    public static void shutdown(){
        workerGroup.shutdownGracefully();
    }

    private static ResponseHandler responseHandler = new ResponseHandler();

    public HttpClient(ResponseHandler responseHandler){

        HttpClient.responseHandler = responseHandler;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        HttpClient.session_id = session_id;
    }

    private static String session_id =null;



    public static void sendCreateRoomRequest(String roomName) {
        try {
            sendRequest("http://localhost:8000/api/room/create", roomName, "", "", HttpMethod.POST);
        } catch (URISyntaxException | InterruptedException e) {
            log.error("Failed to send room create request", e);
        }
    }

    public static void sendLoginRequest(String username, String password) throws URISyntaxException {
        try {
            sendRequest("http://localhost:8000/api/user/login", username, password, "", HttpMethod.GET);
        } catch (URISyntaxException e) {
            log.error("Failed to send login request", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sendRegisterRequest(String username, String password, String email){
        try {
            sendRequest("http://localhost:8000/api/user/register", username, password, email,HttpMethod.POST );
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sendRequest(String url, String username, String password, String email, HttpMethod method) throws URISyntaxException, InterruptedException {
        URI uri = new URI(url);
        String host = uri.getHost();
        int port = uri.getPort();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new HttpClientCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                ch.pipeline().addLast(new HttpClient(responseHandler));
            }
        });
        ChannelFuture connectFuture = b.connect(host, port);
        connectFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                Channel ch = future.channel();
                String content = String.format("{\"username\": \"%s\", \"password\": \"%s\", \"email\": \" %s\" }", username, password, email);
                // HTTP 요청 준비
                DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, method, uri.getRawPath(),
                        Unpooled.wrappedBuffer(content.getBytes()));
                request.headers().set(HttpHeaderNames.HOST, host);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
                request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
                if (session_id != null) {
                    request.headers().set("Cookie", "sessionid=" + session_id);
                }
                ChannelFuture channelFuture = ch.writeAndFlush(request);
                channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                    if (channelFuture1.isSuccess()) {
                        System.out.println("Request sent");
                    } else {
                        Throwable cause = channelFuture1.cause();
                        cause.printStackTrace();
                    }
                });
                System.out.println("Connected to server");
            } else {
                Throwable cause = future.cause();
                cause.printStackTrace();
            }
        });
    }

    public static void sendIdDuplicateCheckRequest(String username) {
        try {
            sendRequest("http://localhost:8000/api/check/id/", username, "", "",HttpMethod.GET );
        } catch (URISyntaxException | InterruptedException e) {
            log.error("Failed to send id duplicate check request", e);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse response) {
            // 응답 상태 코드 처리
            String sessionHeader = response.headers().get("Set-Cookie");
            if(sessionHeader != null){
                String[] session = sessionHeader.split(";");
                String[] session_id = session[0].split("=");
                setSession_id(session_id[1]);
            }
            log.info("session_id: " + getSession_id());
            log.info("STATUS: " + response.status());
            // 응답 헤더 처리
            HttpHeaders headers = response.headers();
            log.info("HEADERS: " + headers.toString());
        }
        if (msg instanceof HttpContent content) {
            // 응답 본문 처리
            ByteBuf byteBuf = content.content();
            if(responseHandler != null){
                responseHandler.handleResponseContent(byteBuf);
            }else{
                log.info("Response handler is null");
            }

            new ResponseHandler().handleResponseContent(byteBuf);
            log.info("CONTENT: " + byteBuf);
            if (content instanceof LastHttpContent) {
                ctx.close(); // 연결 종료
            }
        }
    }

}
