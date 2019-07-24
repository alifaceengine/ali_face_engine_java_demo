import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class Utils {
    public static final String VENDOR_KEY = "eyJ2ZW5kb3JJZCI6ImNlc2hpX3ZlbmRvciIsInJvbGUiOjIsImNvZGUiOiJBNEU1QzZCNkMxQkY4RkZENjgwRTY2NkIzMkIxNjI2RSIsImV4cGlyZSI6IjIwMTkwODMxIiwidHlwZSI6MX0=";
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
}
