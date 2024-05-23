package client;

import channelinfo.ChannelInfoListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class Client {

    private static String user;

    public Client(String user) {
        this.user = user;
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8080;
        Scanner sc = new Scanner(System.in);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(
                            new StringDecoder(),
                            new ClientEventHandler(),
                            new StringEncoder());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();
            f.addListener(new ChannelInfoListener("connected to server"));
            messageLoop(sc.reset(), f.channel());
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static void messageLoop(Scanner scanner, Channel channel) {
        if (user == null) {
            System.out.print("your name: ");
            user = scanner.nextLine();
        }

        while (true) {
            System.out.print("> ");
            String message = scanner.nextLine();
            if (message.equals("exit")){
                break;
            }
            ChannelFuture sent = channel.writeAndFlush(user + ";" + message);
            sent.addListener(new ChannelInfoListener("message sent"));
        }
    }
}
