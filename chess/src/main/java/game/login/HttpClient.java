package game.login;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import javax.crypto.SecretKey;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class HttpClient extends SimpleChannelInboundHandler<HttpObject> {
    public HttpClient(){

    }


    public static void sendLoginRequest(String username, String password){
        try {
            sendRequest("http://localhost:8000/api/login/", username, password, "", HttpMethod.GET);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sendRegisterRequest(String username, String password, String email){
        try {
            sendRequest("http://localhost:8000/api/register/", username, password, email,HttpMethod.POST );
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sendRequest(String url, String username, String password, String email, HttpMethod method) throws URISyntaxException, InterruptedException {
        URI uri = new URI(url);
        String host = uri.getHost();
        int port = uri.getPort();

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(new HttpClientCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                    ch.pipeline().addLast(new HttpClient());
                }
            });

            // Start the client.
            Channel ch = b.connect(host, port).sync().channel();

            // 로그인 데이터를 JSON 형식으로 준비
            String content = String.format("{\"username\": \"%s\", \"password\": \"%s\", \"email\": \" %s\" }", username, password,email);

            // HTTP 요청 준비
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, method, uri.getRawPath(),
                    Unpooled.wrappedBuffer(content.getBytes()));
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

            // Send the HTTP request.
            ChannelFuture channelFuture = ch.writeAndFlush(request);

            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if (channelFuture1.isSuccess()) {

                    System.out.println("Request sent");
                } else {
                    Throwable cause = channelFuture1.cause();
                    cause.printStackTrace();
                }
            });
            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void sendIdDuplicateCheckRequest(String username) {
        try {
            sendRequest("http://localhost:8000/api/check_id/", username, "", "",HttpMethod.GET );
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse response) {
            // 응답 상태 코드 처리
            String authHeader = response.headers().get(HttpHeaderNames.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwtToken = authHeader.substring(7);
                if(validateToken(jwtToken)) {
                    System.out.println("Valid Token");
                }
            }
            System.out.println("STATUS: " + response.status());
            // 응답 헤더 처리
            HttpHeaders headers = response.headers();
            System.out.println("HEADERS: " + headers.toString());
        }
        if (msg instanceof HttpContent content) {
            // 응답 본문 처리
            ByteBuf buf = content.content();
            System.out.println("CONTENT: " + buf.toString(CharsetUtil.UTF_8));
            String response = buf.toString(CharsetUtil.UTF_8);
            if(response != null&&response.contains("token")){
                String token = response.substring(7);
                System.out.println("Authorization: " + token);

            }
            responseAction(response);
            buf.release();
            // 마지막 컨텐츠 조각인 경우
            if (content instanceof LastHttpContent) {
                ctx.close(); // 연결 종료
            }
        }
    }

    private void responseAction(String response) {

    }
    SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    String secretKeyString = Encoders.BASE64.encode(secretKey.getEncoded());
    private boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKeyString).build().parseClaimsJws(jwtToken);
            return true;
        } catch (Exception e) {
            // 토큰 검증 실패 시 예외 처리
            return false;
        }
    }
}