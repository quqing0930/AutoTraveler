package pers.traveler.tools;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created by quqing on 16/5/4.
 */
public class MD5Generator {
    /**
     * 计算指定文件的MD5签名
     *
     * @param fileName
     * @return MD5
     */
    public static String getMD5ByFile(String fileName) throws Exception {
        String value = null;
        File file = new File(fileName);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return value;
    }

    /**
     * 计算指定字符串的MD5签名
     *
     * @param content
     * @return MD5
     * @throws Exception
     */
    public static String getMD5ByString(String content) throws Exception {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes());
            BigInteger bi = new BigInteger(1, md5.digest());
            return bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 计算指定字符串的MD5签名
//     *
//     * @param content
//     * @return MD5
//     * @throws Exception
//     */
//    public static String getMD5ByString(String content) throws Exception {
//        try {
//            //生成实现指定摘要算法的 MessageDigest 对象。
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            //使用指定的字节数组更新摘要。
//            md.update(content.getBytes());
//            //通过执行诸如填充之类的最终操作完成哈希计算。
//            byte b[] = md.digest();
//            //生成具体的md5密码到buf数组
//            int i;
//            StringBuffer buf = new StringBuffer("");
//            for (int offset = 0; offset < b.length; offset++) {
//                i = b[offset];
//                if (i < 0)
//                    i += 256;
//                if (i < 16)
//                    buf.append("0");
//                buf.append(Integer.toHexString(i));
//            }
//            return buf.toString();
////            16位的加密
////            System.out.println("16位: " + buf.toString().substring(8, 24));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
