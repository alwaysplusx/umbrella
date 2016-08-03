package com.harmony.umbrella.ee.spi.wildfly;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import com.harmony.umbrella.ee.formatter.AbstractJndiFormatter.Formatter;
import com.harmony.umbrella.ee.formatter.DefaultFormatterFactory;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.resource.UrlResource;
import com.harmony.umbrella.util.AntPathMatcher;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.PathMatcher;
import com.harmony.umbrella.util.ResourceUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.util.XmlUtils;

/**
 * @author wuxii@foxmail.com
 */
public class WildflyFormatterFactory extends DefaultFormatterFactory {

    private String applicationName;

    private List<String> classesDirectoryNames = Arrays.asList("target/classes");

    private PathMatcher packageMatcher = new AntPathMatcher(".");

    @SuppressWarnings("rawtypes")
    private Map<Class, String> classModuleMapping = new HashMap<Class, String>();

    private Map<String, String> packageModuleMapping = new LinkedHashMap<String, String>();

    public WildflyFormatterFactory() {
    }

    @Override
    protected Formatter createFormatter(String pattern) {
        return new WildflyFormatter(pattern);
    }

    public String getApplicationName() {
        if (applicationName == null) {
            Resource resource = getApplicationXMLResource();
            if (resource != null) {
                applicationName = findApplicationNameByApplicationXmlResource(resource);
            }
        }

        if (applicationName == null) {
            try {
                Enumeration<URL> resources = ClassUtils.getDefaultClassLoader().getResources("");
                while (resources.hasMoreElements() && applicationName == null) {
                    applicationName = findNameByResourceUrl(resources.nextElement(), ".ear");
                }
            } catch (IOException e) {
            }
        }

        return applicationName;
    }

    private Resource getApplicationXMLResource() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource("META-INF/application.xml");
        if (url == null) {
            ClassLoader cl2 = WildflyFormatterFactory.class.getClass().getClassLoader();
            if (cl != cl2) {
                url = cl.getResource("META-INF/application.xml");
            }
        }
        if (url == null) {
            return null;
        }
        return new UrlResource(url);
    }

    private String findApplicationNameByApplicationXmlResource(Resource resource) {
        String applicationName = null;
        InputStream is = null;
        try {
            is = resource.getInputStream();
            Document doc = XmlUtils.getDocument(is, true);
            is.close();
            applicationName = XmlUtils.getAttribute(doc, "application/application-name");
        } catch (Exception e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

        if (applicationName == null) {
            try {
                applicationName = findNameByResourceUrl(resource.getURL(), ".ear");
            } catch (IOException e) {
            }
        }

        return applicationName;
    }

    private String findNameByResourceUrl(URL url, String extension) {
        String path = url.getPath();

        int extensionIndex = path.lastIndexOf(extension);

        if (extensionIndex != -1) {
            path = path.substring(0, extensionIndex);
            int separator = path.lastIndexOf("/");
            return path.substring(separator + 1, extensionIndex);
        }

        return null;
    }

    public Map<String, String> getPackageModuleMapping() {
        return packageModuleMapping;
    }

    public void setPackageModuleMapping(Map<String, String> packageModuleMapping) {
        this.packageModuleMapping = packageModuleMapping;
    }

    @SuppressWarnings("rawtypes")
    public Map<Class, String> getClassModuleMapping() {
        return classModuleMapping;
    }

    @SuppressWarnings("rawtypes")
    public void setClassModuleMapping(Map<Class, String> classModuleMapping) {
        this.classModuleMapping = classModuleMapping;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public List<String> getClassesDirectoryNames() {
        return classesDirectoryNames;
    }

    public void setClassesDirectoryNames(List<String> classesDirectoryNames) {
        this.classesDirectoryNames = classesDirectoryNames;
    }

    public URL getClassUrl(Class<?> beanInterface) {
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
        String path = ClassUtils.classPackageAsResourcePath(beanInterface);
        return cl.getResource(path + "/" + beanInterface.getSimpleName() + ClassUtils.CLASS_FILE_SUFFIX);
    }

    public class WildflyFormatter extends DefaultFormatterFactory.StringPatternFormatter implements Formatter {

        public WildflyFormatter(String pattern) {
            super(pattern);
        }

        @Override
        public String doFormat(String globalNamespace, String beanName, String separator, Class<?> beanInterface) {
            if (StringUtils.isNotBlank(globalNamespace)) {
                if (globalNamespace.indexOf("{app_name}") != -1) {
                    String applicationName = getApplicationName();
                    if (StringUtils.isBlank(applicationName)) {
                        throw new IllegalStateException("can't find application name");
                    }
                    globalNamespace = globalNamespace.replace("{app_name}", applicationName);
                }
                if (globalNamespace.indexOf("{module_name}") != -1 && beanInterface != null) {
                    String moduleName = getModuleName(beanInterface);
                    if (StringUtils.isBlank(moduleName)) {
                        throw new IllegalStateException("can't find module name");
                    }
                    globalNamespace = globalNamespace.replace("{module_name}", moduleName);
                }
            }
            return super.doFormat(globalNamespace, beanName, separator, beanInterface);
        }

        public String getModuleName(Class<?> beanInterface) {
            String moduleName = classModuleMapping.get(beanInterface);

            if (moduleName == null) {
                moduleName = getModuleNameByPackage(beanInterface.getPackage().getName());
            }

            if (moduleName == null) {
                URL url = getClassUrl(beanInterface);
                if (url != null) {
                    moduleName = findNameByResourceUrl(url, ".jar");
                    if (moduleName == null && ResourceUtils.isFileURL(url)) {
                        for (String dirName : classesDirectoryNames) {
                            moduleName = findNameByResourceUrl(url, dirName);
                            if (moduleName != null) {
                                break;
                            }
                        }
                    }
                }
            }

            if (moduleName != null) {
                classModuleMapping.put(beanInterface, moduleName);
            }

            return moduleName;
        }

        private String getModuleNameByPackage(String packageName) {
            Set<String> packages = packageModuleMapping.keySet();
            for (String pkg : packages) {
                if (packageMatcher.match(pkg, packageName)) {
                    return packageModuleMapping.get(pkg);
                }
            }
            return null;
        }
    }
}
