package cloud.server.serverHandlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileServerHandler extends ChannelInboundHandlerAdapter {
    private final Path USERPATH = Paths.get(".", "serverUsers", "user", "1.mkv");
    private ByteBuf accumulator;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
        accumulator = allocator.directBuffer(1024 * 1024, 1024 * 1024 * 10);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        accumulator.writeBytes(in);
        in.release();

        Files.write(USERPATH, ByteBufUtil.getBytes(accumulator), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        accumulator.clear();
        /*ChannelFuture future = ctx.writeAndFlush("ok".getBytes());
        future.addListener(ChannelFutureListener.CLOSE);*/
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        accumulator.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
