package cloud.server.serverHandlers;

import cloud.common.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSenderServerHandler extends ChannelInboundHandlerAdapter {
    private final Path USERPATH = Paths.get(".", "serverUsers", "user");
    private int fileNameLength;
    private String fileName;
    private State state = State.READTAG;
    private RandomAccessFile raf;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        long length = -1;
        String tag;

        if (in.readableBytes() >= 3) {
            if (state == State.READTAG) {
                tag = in.readSlice(3).toString(StandardCharsets.UTF_8);
                if (tag.equals(Command.DOWNLOAD.getTag())) {
                    System.out.println(tag);
                    state = State.READFILENAMELENGTH;
                } else {
                    ctx.fireChannelRead(msg);
                }
            }
        }

        if (state == State.READFILENAMELENGTH) {
            if (in.readableBytes() >= 2) {
                fileNameLength = Integer.valueOf(in.readSlice(2).toString(StandardCharsets.UTF_8));
                System.out.println(fileNameLength);
                state = State.READFILENAME;
            }
        }

        if (state == State.READFILENAME) {
            if (in.readableBytes() >= fileNameLength) {
                fileName = String.valueOf(in.readSlice(fileNameLength).toString(StandardCharsets.UTF_8));
                System.out.println(fileName);
                try {
                    ctx.writeAndFlush(Command.DOWNLOAD.getTag() + "\n");
                    raf = new RandomAccessFile(USERPATH.toString() + fileName, "rw");
                    length = raf.length();
                    ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } finally {
                    if (length < 0 && raf != null) {
                        raf.close();
                    }
                }
            }
            ctx.writeAndFlush("\n");
            state = State.READTAG;
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private enum State {
        READTAG,
        READFILENAMELENGTH,
        READFILENAME
    }
}
