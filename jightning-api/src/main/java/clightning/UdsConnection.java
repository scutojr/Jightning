package clightning;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class UdsConnection extends ChannelInboundHandlerAdapter {
    private static byte[] EOR = "\n\n".getBytes();

    private SettableFuture<byte[]> future;
    private byte[] request;
    private byte[] response;
    private int pos;

    public UdsConnection(byte[] request) {
        pos = 0;
        response = new byte[16384];
        this.request = request;
        this.future = SettableFuture.create();
    }

    public byte[] getResponse() throws ExecutionException, InterruptedException {
        return future.get();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buffer = Unpooled.wrappedBuffer(request);
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        try {
            int cnt = buffer.readableBytes();
            if (cnt > response.length - pos) {
                response = Arrays.copyOf(response, response.length + cnt);
            }
            buffer.readBytes(response, pos, cnt);
            pos += cnt;
            int i = lastIndexOf(response, pos, EOR);
            if (i >= 0) {
                future.set(Arrays.copyOf(response, pos));
                ctx.close();
            }
        } finally {
            ReferenceCountUtil.release(msg); // TODO: know more about this
        }
    }

    private int lastIndexOf(byte[] array, int len, byte[] pattern) {
        int pos = array.length - pattern.length;
        while (pos >= 0) {
            boolean flag = true;
            for (int i = 0; i < pattern.length; i++) {
                if (array[pos + i] != pattern[i]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return pos;
            }
            pos -= 1;
        }
        return -1;
    }
}
