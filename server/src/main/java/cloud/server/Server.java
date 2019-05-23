package cloud.server;

import cloud.server.serverHandlers.AuthServerHandler;
import cloud.server.serverHandlers.FileAcceptorServerHandler;
import cloud.server.serverHandlers.FileListUpdaterServerHandler;
import cloud.server.serverHandlers.FileSenderServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.file.Path;
import java.nio.file.Paths;

class Server {
    private static Path UserPath;

    public static Path getUserPath() {
        return UserPath;
    }

    public static void setUserPath(String userFolder) {
        Server.UserPath = Paths.get(".", "serverUsers", userFolder);
    }

    void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new AuthServerHandler(),
                                    new StringEncoder(),
                                    new FileAcceptorServerHandler(),
                                    new FileSenderServerHandler(),
                                    new FileListUpdaterServerHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(8189).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
