package pers.traveler.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * 读取指定文件内容
     *
     * @param fileName
     * @return 将读取到的内容作为String类型返回
     * @throws IOException 文件操作异常
     */
    public static String readAll(String fileName)
            throws IOException {
        java.io.InputStream ins = new java.io.FileInputStream(fileName);
        // 根据ins中的字节长度 创建一个byte数组保存读出的数据
        byte[] contentByte = new byte[ins.available()];
        // 对contentByte数组赋值
        // 读入就是把文件中的东西读出后读入到另一个东西里去
        ins.read(contentByte);
        String s = new String(contentByte);
        ins.close();
        return s;
    }

    /**
     * 内容写入指定文件
     *
     * @param content
     * @throws IOException 文件操作异常
     */
    public static void writeAll(String fileName, String content)
            throws IOException {
        java.io.OutputStream os = new java.io.FileOutputStream(fileName);
        byte[] contentByte = content.getBytes();
        os.write(contentByte);
        os.close();
    }

    /**
     * @param fname 文件名
     * @return
     * @throws FileNotFoundException
     * @author QUQING
     * @整块读取文件内容
     */
    public static String readResource(String fname) throws FileNotFoundException, IOException {
        String content = "";
        InputStream is = FileUtil.class.getResourceAsStream(fname);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = reader.readLine()) != null) {
            content += line + "\n";
        }
        reader.close();
        return content;
    }

    public static void makeDir(File dir) {
        if (dir.getParentFile() != null && !dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    public static void createDir(String dir) {
        File file = new File(dir);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
    }

    /**
     * @param file
     * @return
     * @throws IOException 创建文件，目录不存在则自动创建
     * @author quqing
     */
    public static boolean createFile(File file) throws IOException {
        if (!file.exists()) {
            makeDir(file.getParentFile());
        }
        return file.createNewFile();
    }

    public static boolean isExists(String fileName) {
        File file = new File(fileName);
        return file.isFile() && file.exists();
    }

    /**
     * 删除单个文件
     *
     * @param fileName 被删除文件的文件名
     */
    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

    /**
     * @param dir
     * @param suffix 根据指定目录和后缀读取文件列表
     * @author quqing
     */
    public static List<String> getFiles(String dir, String suffix) {
        File baseDir = new File(dir);
        final String suf = suffix;
        List<String> fileList = new ArrayList<String>();
        if (baseDir.isDirectory()) {
            File[] files = baseDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("." + suf);
                }
            });
            for (int i = 0; i < files.length; i++) {
                fileList.add(files[i].getName());
            }
        }
        return fileList;
    }

    /**
     * 递归查找文件夹
     *
     * @param baseDirName   查找的文件夹路径
     * @param targetDirName 需要查找的文件夹名
     * @param dirList       查找到的文件夹集合
     */
    public static void findFiles(String baseDirName, String targetDirName, List<String> dirList) {
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        }
        String tempName = null;
        File tempFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            if (tempFile.isDirectory()) {
                tempName = tempFile.getName();
                if (tempName.equals(targetDirName)) {
                    dirList.add(tempFile.getAbsolutePath() + File.separator);
                } else {
                    findFiles(tempFile.getAbsolutePath(), targetDirName, dirList);
                }
            } else {
                continue;
            }
        }
    }
}