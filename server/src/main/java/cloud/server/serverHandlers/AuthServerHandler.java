package cloud.server.serverHandlers;

import cloud.common.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class AuthServerHandler extends ChannelInboundHandlerAdapter {
    private State state;
    private int loginLength;
    private int passwordLength;
    private String login;
    private String password;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        String tag;

        if (in.readableBytes() >= 3) {
            if (state == State.READTAG) {
                tag = in.readSlice(3).toString(StandardCharsets.UTF_8);
                if (tag.equals(Command.AUTH.getTag())) {
                    System.out.println(tag);
                    state = State.READLOGINLENGTH;
                } else {
                    ctx.fireChannelRead(msg);
                }
            }
        }

        if (in.readableBytes() >= 2) {
            if (state == State.READLOGINLENGTH) {
                loginLength = Integer.valueOf(in.readSlice(2).toString(StandardCharsets.UTF_8));
                System.out.println(loginLength);
                state = State.READLOGIN;
            }
        }

        if (in.readableBytes() >= loginLength) {
            if (state == State.READLOGIN) {
                login = in.readSlice(loginLength).toString(StandardCharsets.UTF_8);
                System.out.println(loginLength);
                state = State.READPASSWORDLENGTH;
            }
        }

        if (in.readableBytes() >= 2) {
            if (state == State.READPASSWORDLENGTH) {
                passwordLength = Integer.valueOf(in.readSlice(2).toString(StandardCharsets.UTF_8));
                System.out.println(passwordLength);
                state = State.READPASSWORD;
            }
        }

        if (in.readableBytes() >= passwordLength) {
            if (state == State.READPASSWORD) {
                password = in.readSlice(passwordLength).toString(StandardCharsets.UTF_8);
                System.out.println(loginLength);
                state = State.READTAG;
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private enum State {
        READTAG,
        READLOGINLENGTH,
        READLOGIN,
        READPASSWORDLENGTH,
        READPASSWORD
    }
}
