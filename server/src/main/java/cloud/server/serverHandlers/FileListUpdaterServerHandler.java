package cloud.server.serverHandlers;

import cloud.common.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class FileListUpdaterServerHandler extends ChannelInboundHandlerAdapter {
    private final Path USERPATH = Paths.get(".", "serverUsers", "user");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        String tag;
        ArrayList<String> fileList = new ArrayList<>();

        if (in.readableBytes() >= 3) {
            tag = in.readSlice(3).toString(StandardCharsets.UTF_8);
            if (tag.equals(Command.UPGRADE.getTag())) {
                try {
                    Files.walkFileTree(USERPATH, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            fileList.add(file.getFileName().toString());
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ctx.writeAndFlush(Command.UPGRADE.getTag() + "\n");
        for (String s : fileList) {
            ctx.writeAndFlush(s + "\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
