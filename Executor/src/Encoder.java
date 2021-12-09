import java.nio.ByteBuffer;

public class Encoder {
    private static final int lenIntInBytes = 4;
    private static final int MaxUnsignedBytePlusOne = 256;
    private static final byte plus = 1;
    private static final byte minus = -1;
    private static byte[] extraBytes = null;

    private static int num;

    Encoder(int n){
        num = n;
    }

    public static byte[] Code(byte[] buffer){
        byte [] res = new byte[buffer.length * lenIntInBytes];
        for(int i = 0; i < buffer.length; ++i){
            byte [] bb = intToBytes(num * buffer[i]);
            for(int j = 0; j < lenIntInBytes; ++j){
                res[i * lenIntInBytes + j] = bb[j];
            }
        }
        return res;
    }

    public static byte[] Decode(byte[] buffer){
        byte[] bytes = null;
        byte[] res = null;
        int rest = buffer.length % lenIntInBytes;
        if(rest != 0){
            if(extraBytes != null){
                byte[] tmp = new byte[buffer.length + extraBytes.length];
                System.arraycopy(extraBytes, 0, tmp, 0, extraBytes.length);
                System.arraycopy(buffer, 0, tmp, extraBytes.length, buffer.length);
                int restTmp = tmp.length % 4;
                if(restTmp != 0){
                    bytes = new byte [tmp.length - restTmp];
                    System.arraycopy(tmp, 0, bytes, 0, bytes.length);
                    extraBytes = new byte[restTmp];
                    System.arraycopy(tmp, bytes.length, extraBytes, 0, restTmp);
                }
                else {
                    extraBytes = null;
                    bytes = tmp;
                }

            }
            else{
                bytes = new byte [buffer.length - rest];
                System.arraycopy(buffer, 0, bytes, 0, bytes.length);
                extraBytes = new byte[rest];
                System.arraycopy(buffer, bytes.length, extraBytes, 0, rest);
            }
            res = DecodeCommon(bytes);
        }else {
            res = DecodeCommon(buffer);
        }
        return res;
    }

    private static byte[] DecodeCommon(byte[] buffer) {
        long codedNum;
        long MaxInt = 4294967296L;
        int resNum;
        long tmp = 0;
        long tmpL = 0;
        long tmpR = 0;
        int sign = plus;
        int lenRes = buffer.length / lenIntInBytes;
        byte [] res = new byte[lenRes];
        for(int i = 0; i < lenRes; ++i){
            codedNum = 0;
            for(int j = 0; j < lenIntInBytes; ++j){
                if(j == 0) {
                    sign = sign(buffer[lenIntInBytes * i]);
                }
                tmpL = pow256(lenIntInBytes - (j + 1));  //to calculate degree
                tmpR = absMod(buffer[lenIntInBytes * i + j]);
                tmp =  tmpL * tmpR;
                codedNum += tmp;
            }
            if(sign == minus){
                resNum = sign * (int)(MaxInt - codedNum);
            }
            else {
                resNum = (int)codedNum;
            }

            res[i] = (byte)(resNum / num);
        }
        return res;
    }

    private static byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    private static long pow256(int num){
        int res = plus;
        for (int i = 0; i < num; ++i)
            res *= MaxUnsignedBytePlusOne;
        return res;
    }

    private static long absMod (byte num) {
        if (num < 0){
            int res = MaxUnsignedBytePlusOne + num;
            return res;
        }
        return (int)num;
    }

    private static byte sign (byte num) {
        if (num < 0)
            return minus;

        return plus;
    }
}
