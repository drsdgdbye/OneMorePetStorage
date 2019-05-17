package cloud.server.serverHandlers;

import cloud.common.Command;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileServerHandler extends ChannelInboundHandlerAdapter {
    private final Path USERPATH = Paths.get(".", "serverUsers", "user");
    private ByteBuf accumulator;
    private String tag;
    private State state;
    private int fileNameLength;
    private String fileName;
    private long fileLength;

    //зарелизить msg
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        state = State.READTAG;

        if (in.readableBytes() < 3) {
            return;
        } else {
            tag = in.readSlice(3).toString(StandardCharsets.UTF_8);
            if (tag.equals(Command.ADD.getTag())) {
                state = State.READFILENAMELENGTH;
            } else {
                ctx.fireChannelRead(msg);
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
                state = State.READFILELENGTH;
            }
        }

        if (state == State.READFILELENGTH) {
            if (in.readableBytes() >= 11) {
                fileLength = Long.valueOf(in.readSlice(11).toString(StandardCharsets.UTF_8));
                System.out.println(fileLength);
                state = State.READFILE;
            }
        }

        if (state == State.READFILE) {
            Path pathFile = Paths.get(USERPATH.toString(), fileName);
            long downloadCount = fileLength;
            accumulator.writeBytes(in);
            Files.write(pathFile, ByteBufUtil.getBytes(accumulator), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            accumulator.clear();
            in.release();
            downloadCount -= ByteBufUtil.getBytes(accumulator).length;
            System.out.println(downloadCount);
            if (downloadCount == 0) {
                state = State.READTAG;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
        accumulator = allocator.directBuffer(1024 * 1024, 1024 * 1024 * 10);
    }

    private enum State {
        READTAG,
        READFILENAMELENGTH,
        READFILENAME,
        READFILELENGTH,
        READFILE

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
