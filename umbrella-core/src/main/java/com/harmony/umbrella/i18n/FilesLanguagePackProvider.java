package com.harmony.umbrella.i18n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.harmony.umbrella.util.StringUtils;

/**
 * 文件类型的语言包提供程序
 * 
 * @author wuxii@foxmail.com
 */
public class FilesLanguagePackProvider implements LanguagePackProvider {

    /**
     * 语言包文件所在的目录
     */
    protected File languagePackDirectory;

    /**
     * 语言包文件名的前缀
     */
    protected String fileNamePrefix;

    /**
     * 语言包文件名的分隔符
     */
    protected String fileNameSeparator;

    /**
     * 读取文件的字符集
     */
    protected String charset;

    /**
     * 开发模式(每次获取主动加载一次)
     */
    protected boolean devMode;

    /**
     * 语言与文件的对应关系
     */
    private Map<Locale, File> localeFileMapping;

    /**
     * 缓存的语言包
     */
    private Map<Locale, LanguagePack> languagePackCache;

    public FilesLanguagePackProvider(File dir) {
        this(dir, "Message", "_", "utf-8", false);
    }

    public FilesLanguagePackProvider(String dir) {
        this(new File(dir), "Message", "_", "utf-8", false);
    }

    public FilesLanguagePackProvider(File dir, String prefix, String separator, String charset, boolean devMode) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir.getPath() + " not directory");
        }
        this.languagePackDirectory = dir;
        this.fileNamePrefix = prefix;
        this.fileNameSeparator = separator;
        this.charset = charset;
        this.devMode = devMode;
    }

    @Override
    public Set<Locale> getAvailableLanguages() {
        return getLocaleFileMapping().keySet();
    }

    @Override
    public LanguagePack getLanguagePack(Locale locale) {
        return findLanguagePack(locale);
    }

    protected List<File> getLanguagePackFiles() {
        List<File> result = new ArrayList<>();
        File[] files = languagePackDirectory.listFiles();
        for (File file : files) {
            if (file.getName().startsWith(fileNamePrefix) && file.isFile()) {
                result.add(file);
            }
        }
        return result;
    }

    protected Map<Locale, File> getLocaleFileMapping() {
        if (localeFileMapping == null || devMode) {
            Map<Locale, File> temp = new HashMap<>();
            for (File file : getLanguagePackFiles()) {
                Locale locale = toLocale(file);
                temp.put(locale, file);
            }
            localeFileMapping = temp;
        }
        return localeFileMapping;
    }

    private LanguagePack findLanguagePack(Locale locale) {
        LanguagePack result = null;
        if (languagePackCache != null && !devMode) {
            result = languagePackCache.get(locale);
        }
        if (result == null) {
            try {
                result = buildLanguagePack(locale);
                if (languagePackCache == null) {
                    languagePackCache = new ConcurrentHashMap<>();
                }
                languagePackCache.put(locale, result);
            } catch (IOException e) {
                throw new IllegalStateException("build language pack failed", e);
            }
        }
        return result;
    }

    protected LanguagePack buildLanguagePack(Locale locale) throws IOException {
        Map<Locale, File> mapping = getLocaleFileMapping();
        File file = mapping.get(locale);
        return toLanguagePack(file);
    }

    /**
     * 把文件解析成对应的语言包
     * 
     * @param file
     *            语言包文件
     * @return 语言包
     * @throws IOException
     */
    protected LanguagePack toLanguagePack(File file) throws IOException {
        Locale locale = toLocale(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));

        Map<String, String> pack = new HashMap<>();
        String line = null;
        do {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("#") || StringUtils.isBlank(line)) {
                continue;
            }
            int index = line.indexOf("=");
            String key = index == -1 ? line : line.substring(0, index);
            String val = index == -1 ? "" : line.substring(index + 1, line.length());
            pack.put(key, val);
        } while (true);

        reader.close();

        return MessageBundle.pack(locale, pack);
    }

    /**
     * 通过切割文件名来得出对应的语言
     * 
     * @param file
     *            文件
     * @return 语言
     */
    protected Locale toLocale(File file) {
        String name = file.getName();
        int dotIndex = name.indexOf(".");
        int beginIndex = (fileNamePrefix + fileNameSeparator).length();
        int endIndex = dotIndex == -1 ? name.length() : dotIndex;
        return MessageBundle.toLocale(name.substring(beginIndex, endIndex));
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public String getFileNameSeparator() {
        return fileNameSeparator;
    }

    public void setFileNameSeparator(String fileNameSeparator) {
        this.fileNameSeparator = fileNameSeparator;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

}
