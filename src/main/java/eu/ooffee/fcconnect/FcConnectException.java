package eu.ooffee.fcconnect;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

public class FcConnectException extends OAuthSystemException {
	
	private static final long serialVersionUID = 1L;

	public FcConnectException() {
		super();
	}

	public FcConnectException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public FcConnectException(String s) {
		super(s);
	}

	public FcConnectException(Throwable throwable) {
		super(throwable);
	}
	
	
	
}
