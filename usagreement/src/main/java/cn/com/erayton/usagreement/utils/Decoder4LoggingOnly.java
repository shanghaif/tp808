package cn.com.erayton.usagreement.utils;

import android.util.Log;

import java.util.List;
import java.util.logging.Logger;

/**
 * 该解码器只是为了自己日志所用,没其他作用.<br>
 * 最终删除
 * 
 * @author hylexus
 *
 */
public class Decoder4LoggingOnly {
	final String TAG = "Decoder4LoggingOnly" ;

//	@Override
//	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//		String hex = buf2Str(in);
//		Log.i(TAG,"ip={}"+ctx.channel().remoteAddress()+",hex = {}"+hex);
//		Log.d(TAG,"ip={}"+ ctx.channel().remoteAddress()+",hex = {}" +hex);
//
//		ByteBuf buf = Unpooled.buffer();
//		while (in.isReadable()) {
//			buf.writeByte(in.readByte());
//		}
//		out.add(buf);
//	}
//
//	private String buf2Str(ByteBuf in) {
//		byte[] dst = new byte[in.readableBytes()];
//		in.getBytes(0, dst);
//		return HexStringUtils.toHexString(dst);
//	}

	public void decodeHex(byte[] in) throws Exception {
		String hex = HexStringUtils.toHexString(in);
		Log.i(TAG,"hex = {}"+ hex);

	}
}
