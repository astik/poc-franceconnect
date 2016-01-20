package fr.smile.pocfc;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ooffee.fcconnect.FcConnectException;
import eu.ooffee.fcconnect.FcConnection;
import eu.ooffee.fcconnect.FcParamConfig;

public class FranceConnectAuthenticationFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(FranceConnectAuthenticationFilter.class);
	private static String tokenUri = "https://fcp.integ01.dev-franceconnect.fr/api/v1/token";
	private static String authorizationUri = "https://fcp.integ01.dev-franceconnect.fr/api/v1/authorize";
	private static String userInfoUri = "https://fcp.integ01.dev-franceconnect.fr/api/v1/userinfo";
	private static String clientId;
	private static String clientSecret;
	private static String scope = "openid profile";
	private static String state = "test";
	private static String verifParameterId = "nonce";
	private static String verifParameterValue = "toto";

	public void destroy() {
		LOGGER.trace("destroy");
		// nothing to do
	}

	private FcConnection getFcConnection(String redirectUrl) {
		String redirectUri = "http://localhost:8080/poc-fc/fc/cb?redirectUrl=" + URLEncoder.encode(redirectUrl);
		FcParamConfig fpc = new FcParamConfig(tokenUri, authorizationUri, redirectUri, userInfoUri, clientId, clientSecret, scope, state, verifParameterId, verifParameterValue);
		FcConnection fcc = new FcConnection(fpc);
		return fcc;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		LOGGER.trace("doFilter");
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (isAlreadyAuthenticated(httpServletRequest)) {
			LOGGER.trace("isAlreadyAuthenticated");
			chain.doFilter(request, response);
		} else {
			if (isInitialization(httpServletRequest)) {
				LOGGER.trace("doFilter : initialization");
				// TODO fix redirect retrieval
				String redirectUri = httpServletRequest.getRequestURI();
				LOGGER.trace("doFilter : redirectUri={}", redirectUri);
				FcConnection fcc = getFcConnection(redirectUri);
				URI redirect;
				try {
					redirect = fcc.getRedirectUri();
				} catch (FcConnectException e) {
					throw new ServletException(e);
				}
				httpServletResponse.sendRedirect(redirect.toString());
			} else {
				LOGGER.trace("doFilter : get user info");
				String accessToken;
				String redirectUrl = httpServletRequest.getParameter("redirectUrl");
				FcConnection fcc = getFcConnection(redirectUrl);
				try {
					OAuthJSONAccessTokenResponse accessTokenContainer = fcc.getAccessToken(httpServletRequest);
					accessToken = accessTokenContainer.getAccessToken();
					String idTokenHint = accessTokenContainer.getParam("id_token");
					httpServletRequest.getSession().setAttribute("idTokenHint", idTokenHint);
				} catch (FcConnectException e) {
					throw new ServletException(e);
				}
				String userInfo;
				try {
					userInfo = fcc.getUserInfo(accessToken);
				} catch (FcConnectException e) {
					throw new ServletException(e);
				}
				httpServletRequest.getSession().setAttribute("userInfo", userInfo);
				LOGGER.trace("userInfo={}", userInfo);
				if (redirectUrl == null) {
					redirectUrl = httpServletRequest.getContextPath();
				}
				httpServletResponse.sendRedirect(redirectUrl);
			}
		}
	}

	private boolean isAlreadyAuthenticated(HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return httpServletRequest.getSession(false) != null && httpServletRequest.getSession().getAttribute("userInfo") != null;
	}

	private boolean isInitialization(HttpServletRequest httpServletRequest) {
		String servletPath = httpServletRequest.getServletPath();
		LOGGER.trace("servletPath = {}", servletPath);
		return servletPath.lastIndexOf("cb") != servletPath.length() - 2;
	}

	public void init(FilterConfig config) throws ServletException {
		LOGGER.trace("init");
		clientId = config.getInitParameter("clientId");
		clientSecret = config.getInitParameter("clientSecret");
	}
}
