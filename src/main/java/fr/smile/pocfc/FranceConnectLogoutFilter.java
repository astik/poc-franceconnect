package fr.smile.pocfc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FranceConnectLogoutFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(FranceConnectLogoutFilter.class);

	public void destroy() {
		LOGGER.trace("destroy");
		// nothing to do
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		LOGGER.trace("doFilter");
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String logoutUri = "https://fcp.integ01.dev-franceconnect.fr/api/v1/logout";
		String idTokenHint = (String) httpServletRequest.getSession().getAttribute("idTokenHint");
		String state = "toto";
		String postLogoutRedirectUri = "http://localhost:8080/poc-fc/logout.jsp";
		String redirectUri = logoutUri + "?id_token_hint=" + idTokenHint + "&state=" + state + "&post_logout_redirect_uri=" + postLogoutRedirectUri;
		httpServletRequest.getSession().invalidate();
		httpServletResponse.sendRedirect(redirectUri);
	}

	public void init(FilterConfig config) throws ServletException {
		LOGGER.trace("init");
	}
}
