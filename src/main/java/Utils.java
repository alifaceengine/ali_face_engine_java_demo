import java.io.*;
import java.lang.reflect.Field;

public class Utils {
    public static final String VENDOR_KEY = "DVxKR19dWQgCA1FXSVkKFA4XAxcSBEtFEEIXAFkBHRtZTFldXFBeXlZTEVZKXUZAV0UJE04UHx1CHgEZFxQXEhgZOgRICwoMAEJQBkJZUQlFXFwOEhgDSVFGUwgFAgEdUF5YGF5WWwBEQVpQFhAcThEHDBwKSjJMVlFhU1tYUlwUAVwDFFVdU1cJSFBeDU9BAV1OEUZXEhYEShUHWFBBTEcWGgYFB1BcBFE=";
    public static String PICTURE_ROOT = "./pictures/";

    public static byte[] loadFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("file not exist:" + filePath);
            return null;
        }

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }

        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        try {
            FileInputStream fi = new FileInputStream(file);
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            fi.close();
        } catch (IOException e) {
        }

        if (offset != buffer.length) {
            System.out.println("Could not completely read file");
            return null;
        }

        return buffer;
    }

    public static void saveFile(byte[] bfile, String filePath) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
