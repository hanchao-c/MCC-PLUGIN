package com.allinmd.generator.resource;

import java.io.InputStream;
import java.net.URL;

public interface Resource {

    String getAsString();

    InputStream getAsInputStream();

    URL getAsURL();

}
