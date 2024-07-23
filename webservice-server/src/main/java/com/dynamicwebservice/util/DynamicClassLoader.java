package com.dynamicwebservice.util;

import com.zipe.util.classloader.CustomClassLoader;

import java.net.URL;

public class DynamicClassLoader extends CustomClassLoader {

    public DynamicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addURL(URL url) {
        super.addURL(url);
    }

}
