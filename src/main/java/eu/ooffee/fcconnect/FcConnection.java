package eu.ooffee.fcconnect;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FcConnection {
	
    private final Logger log = LoggerFactory.getLogger(getClass());

    
	FcParamConfig configuration;

	public FcConnection(FcParamConfig configuration) {
		super();
		this.configuration = configuration;
	}
	
	
	public URI getRedirectUri() throws FcConnectException{
		
		try {
			OAuthClientRequest request = OAuthClientRequest
						.authorizationLocation(configuration.getAuthorizationUri())
					   .setClientId(configuration.getClientId())
					   .setRedirectURI(configuration.getRedirectUri())
					   .setResponseType(ResponseType.CODE.toString())
					   .setScope(configuration.getScope())
					   .setState(configuration.getState())
					   .setParameter(configuration.getVerifParameterId(), configuration.getVerifParameterValue())
					   .buildQueryMessage();
			
			log.debug(request.getLocationUri());
			
	        return new URI(request.getLocationUri());
			
		} catch (OAuthSystemException e) {
			throw new FcConnectException(e);
		} catch (URISyntaxException e) {
			throw new FcConnectException("The uri is not well formed", e);
		}
		
	}
	
	public OAuthJSONAccessTokenResponse getAccessToken(HttpServletRequest request) throws FcConnectException{
		
		try {
			OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
			
			log.debug("autorization code" + oar.getCode());
			
			//vérification a rajouter par rapport au state pour éviter le man in the middle
//			if (StringUtils.isBlank(stateResponse) || 
//					!stateResponse.equals(stateFromSession)) {
//				
//				res = Response.ok("<p>Erreur de verif state response !!</p>",TEXT_HTML);
//				addCORSOrigin(servletContext,res, headers);
//		    	return res.build();
////				return new RedirectView("/login");
//			}
			
			//récupération de l'access token
			OAuthClientRequest authClientRequest = OAuthClientRequest
					 .tokenLocation(configuration.getTokenUri())
					 .setGrantType(GrantType.AUTHORIZATION_CODE)
					 .setClientId(configuration.getClientId())
					 .setClientSecret(configuration.getClientSecret())
					 .setRedirectURI(configuration.getRedirectUri())
					 .setCode(oar.getCode())
					 .buildBodyMessage();
			
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			
			return oAuthClient.accessToken(authClientRequest);
			
		} catch (OAuthSystemException e) {
			throw new FcConnectException("Error during request for accessToken : ", e);
		} catch (OAuthProblemException e) {
			throw new FcConnectException("Error during accessToken retrieving : ", e);
		}
	}
	
	public String getUserInfo(String accessToken) throws FcConnectException{
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		
		//récupération du profil client
		OAuthClientRequest bearerClientRequest;
		try {
			bearerClientRequest = new OAuthBearerClientRequest(configuration.getUserInfoUri())
			      .setAccessToken(accessToken)
			      .buildHeaderMessage();
			
			OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET,
				      OAuthResourceResponse.class);
			
			return resourceResponse.getBody();
			
		} catch (OAuthSystemException e) {
			throw new FcConnectException("Error during userInfo request building : ", e);
		} catch (OAuthProblemException e) {
			throw new FcConnectException("Error during userInfo retrieving : ", e);
		}
		
	}
	
	

}
