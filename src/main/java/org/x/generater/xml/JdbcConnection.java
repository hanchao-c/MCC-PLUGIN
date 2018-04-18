package org.x.generater.xml;

public class JdbcConnection {

	private final String driverClass = "com.mysql.jdbc.Driver";
	private String url;
	private String userName;
	private String password;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "JdbcConnection [driverClass=" + driverClass + ", url=" + url + ", userName=" + userName + ", password="
				+ password + "]";
	}

	public String getDriverClass() {
		return driverClass;
	}

}
