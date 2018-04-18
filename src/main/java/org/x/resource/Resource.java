package org.x.resource;

import java.io.InputStream;
import java.net.URL;

public interface Resource {

	String getAsString();
	
	InputStream getAsInputStream();
	
	URL getAsURL();
	
}
