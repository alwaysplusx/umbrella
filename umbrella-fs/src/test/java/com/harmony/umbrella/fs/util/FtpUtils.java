package com.harmony.umbrella.fs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.FileUtils;

/**
 * FIXME 中文文件名未解决
 * 
 * @author wuxii@foxmail.com
 */
public class FtpUtils {

    private static final Log log = Logs.getLog(FtpUtils.class);

    public static boolean exists(FTPClient ftp, String pathname) {
        try {
            getFtpFile(ftp, pathname);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFile(FTPClient ftp, String pathname) {
        try {
            return getFtpFile(ftp, pathname).isFile();
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isDirectory(FTPClient ftp, String pathname) {
        try {
            return getFtpFile(ftp, pathname).isDirectory();
        } catch (IOException e) {
            return false;
        }
    }

    public static void getFile(FTPClient ftp, String pathname, OutputStream os) throws IOException {
        ftp.retrieveFile(pathname, os);
    }

    public static void getFile(FTPClient ftp, String pathname, File dest) throws IOException {
        FileOutputStream fos = new FileOutputStream(dest);
        try {
            getFile(ftp, pathname, fos);
        } finally {
            fos.close();
        }
    }

    public static File getFile(FTPClient ftp, String pathname) throws IOException {
        String dir = "ftp/" + UUID.randomUUID().toString().replace("-", "");
        File tempFile = FileUtils.createTempFile(dir);
        getFile(ftp, pathname, tempFile);
        return tempFile;
    }

    public static FTPFile getFtpFile(final FTPClient ftp, String pathname) throws IOException {
        final FileName fileName = new FileName(pathname);
        FTPFile[] files = ftp.listFiles(fileName.parentPath, new FileNameFTPFileFilter(fileName));
        if (files == null || files.length == 0 || files.length > 1) {
            throw new IOException("file not found, " + (files != null ? files.length : "null"));
        }
        return files[0];
    }

    /**
     * 级联创建ftp上的目录
     * 
     * @param ftp
     *            ftp客户端
     * @param pathname
     *            ftp上的目录
     * @throws IOException
     */
    public static void mkdirs(FTPClient ftp, String pathname) throws IOException {
        mkdirs(ftp, pathname, File.separator);
    }

    public static void mkdirs(FTPClient ftp, String pathname, String separator) throws IOException {
        log.info("mark directory [{}/{}] at {}", ftp.printWorkingDirectory(), pathname, ftp.getRemoteAddress());
        StringBuilder currentPath = new StringBuilder();
        StringTokenizer st = new StringTokenizer(pathname, "/\\");
        while (st.hasMoreTokens()) {
            currentPath.append(st.nextToken());
            try {
                FTPFile file = getFtpFile(ftp, currentPath.toString());
                // ensure pathname is directory
                if (!file.isDirectory()) {
                    throw new IOException(currentPath.toString() + " not a directory");
                }
            } catch (IOException e) {
                // directory is not found create it
                ftp.makeDirectory(currentPath.toString());
                while (st.hasMoreElements()) {
                    currentPath.append(separator).append(st.nextToken());
                    ftp.makeDirectory(currentPath.toString());
                }
            }
            currentPath.append(separator);
        }
    }

    private static class FileName {
        String separator;
        final String name;
        String fileName;
        String parentPath;

        public FileName(String name, String separator) {
            this.name = name;
            this.separator = separator;
            init_split();
        }

        public FileName(String name) {
            this(name, "/");
        }

        private void init_split() {
            StringTokenizer st = new StringTokenizer(name, "/\\");
            int countTokens = st.countTokens();
            if (countTokens == 1 || countTokens == 0) {
                fileName = countTokens == 0 ? "" : st.nextToken();
                parentPath = "";
            } else {
                int count = 0;
                StringBuilder path = new StringBuilder();
                while (true) {
                    path.append(st.nextToken()).append(separator);
                    if (++count == countTokens - 1) {
                        fileName = st.nextToken();
                        parentPath = path.toString();
                        break;
                    }
                }

            }
        }
    }

    private static class FileNameFTPFileFilter implements FTPFileFilter {

        private FileName fileName;

        private List<String> originalCharsets = Arrays.asList("ISO-8859-1", "UTF-8", "GBK", "GB2312");
        private List<String> destCharsets = Arrays.asList("GBK", "UTF-8", "GB2312", "ISO-8859-1");

        public FileNameFTPFileFilter(FileName fileName) {
            this.fileName = fileName;
        }

        @Override
        public boolean accept(FTPFile file) {
            String name = file.getName();
            if (name.equals(fileName.fileName)) {
                return true;
            }
            String fileName = file.getName().replace(".", "");
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                    for (String o : originalCharsets) {
                        try {
                            byte[] buff = name.getBytes(o);
                            for (String d : destCharsets) {
                                if (new String(buff, d).equals(this.fileName.fileName)) {
                                    return true;
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                        }
                    }
                    break;
                }
            }
            return false;
        }

    }

}
