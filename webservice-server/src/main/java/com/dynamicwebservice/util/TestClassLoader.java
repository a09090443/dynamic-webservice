package com.dynamicwebservice.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class TestClassLoader extends URLClassLoader {
    public TestClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void close() throws IOException {
        // 釋放資源
        for (URL url : this.getURLs()) {
            JarFile jarFile = new JarFile(new File(url.getFile()));
            jarFile.close();
        }
    }

    public void unloadJarFile(URL jarUrl) throws IOException {
        try {
            for (URL url : this.getURLs()) {
                if (url.equals(jarUrl)) {
                    JarFile jarFile = new JarFile(new File(url.getFile()));
                    jarFile.close();
                    break;
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to unload JAR file", e);
        }
    }

}
