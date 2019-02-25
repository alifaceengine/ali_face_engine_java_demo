import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class Utils {
    public static final String VENDOR_KEY = "eyJ2ZW5kb3JJZCI6ImNlc2hpX3ZlbmRvciIsInJvbGUiOjIsImNvZGUiOiIzRDE5RUIwNjY1OEE5MUExQzlCNDY0MzhDN0QwNDFGMyIsImV4cGlyZSI6IjIwMTkwMzMxIiwidHlwZSI6MX0=";

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

    public static void setAliFaceEngineLibPath() {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String userDir = System.getProperty("user.dir");
        System.out.println("os.name: " + osName + ", os.arch: " + osArch + ", user.dir: " + userDir);

        if (osName == null) {
            throw new RuntimeException("setAliFaceEngineLibPath error, unknown os");
        }

        String AliFaceEngineLibPath = "";
        if (osName.contains("Mac")) {
            AliFaceEngineLibPath = "libs/Darwin/";
        } else if (osName.contains("Windows")) {
            AliFaceEngineLibPath = "libs/Windows/";
            if (osArch.contains("64")) {
                AliFaceEngineLibPath += "x64/";
            } else {
                AliFaceEngineLibPath += "x86/";
            }
        } else {
            throw new RuntimeException("setAliFaceEngineLibPath error, unsupported os");
        }

        System.setProperty("java.library.path", System.getProperty("java.library.path")
                + ":" + userDir + "/" + AliFaceEngineLibPath);
        System.out.println("java.library.path: " + System.getProperty("java.library.path"));

        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("setAliFaceEngineLibPath error, set java.library.path error");
        }
    }
}
