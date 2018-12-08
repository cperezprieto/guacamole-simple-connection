package org.apache.guacamole.net.simpleConnector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.guacamole.GuacamoleClientException;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleSocket;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.apache.guacamole.servlet.GuacamoleHTTPTunnelServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import io.jsonwebtoken.Claims;

public class GuacamoleSimpleConnectorServlet
    extends GuacamoleHTTPTunnelServlet {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(GuacamoleSimpleConnectorServlet.class);
			
	@Override
    protected GuacamoleTunnel doConnect(HttpServletRequest request)
        throws GuacamoleException {
		
		BasicConfigurator.configure();
		
		// Example token
		//?token=eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1Mzk2MTI4NTIsImV4cCI6MTU3MTE0ODMwMCwiaG9zdG5hbWUiOiIxOTIuMTY4LjAuMTUzIiwidXNlcm5hbWUiOiJjYXJsb3MgcGVyZXoiLCJwYXNzd29yZCI6Ik1jMTYxMDk3In0.rThh2L3JpEutC2yYGxR3xmoBsB_NV5rQTR27XyzDfMY
    	
		logger.info("Token: " + request.getParameter("token"));	    

		String hostname = "";
		String port = "3389";
		String username = "";
		String domain = "";
		String password = "";
		String sockethost = System.getenv("GUACD_HOSTNAME");
		String socketport = "4822";
		
		if(request.getParameter("token")!= null) {
			try {
				Claims claims;
				
				claims = JWT.parse(request.getParameter("token"));
				
				if(claims.get("hostname") != null)
					hostname = claims.get("hostname").toString();
		        
				if(claims.get("port") != null)
					port =claims.get("port").toString();
		        
				if(claims.get("username") != null)
					username = claims.get("username").toString();
		        
				if(claims.get("domain") != null)
					domain = claims.get("domain").toString();
		        
				if(claims.get("password") != null)
					password = claims.get("password").toString();
		        
		        if(claims.get("sockethost") != null)
		        	sockethost = claims.get("sockethost").toString();
		        
		        if(claims.get("socketport") != null)
	        		socketport = claims.get("socketport").toString();
			} catch(Exception e) {
				throw new GuacamoleClientException(e.getMessage());
			}
	        
	        GuacamoleConfiguration config = new GuacamoleConfiguration();        
	        
	    	// Create configuration from params    		
	        config.setProtocol("rdp");
	        config.setParameter("security", "any");
	        config.setParameter("ignore-cert", "true");
	        
	        if(!isNullOrEmpty(hostname))
	        	config.setParameter("hostname", hostname);
	    	
	        if(!isNullOrEmpty(port))
	        	config.setParameter("port", port);
	        
	        if(!isNullOrEmpty(username) && !isNullOrEmpty(password)) {
	        	config.setParameter("username", username);
	        	config.setParameter("password", password);
	        	
		    	if(!isNullOrEmpty(domain))
		        	config.setParameter("domain", domain);
	        }
		    	
	    	config.setParameter("hostname", hostname);
	        config.setParameter("port", port);
	        config.setParameter("username", username);
	        config.setParameter("password", password);
	        config.setParameter("domain", domain);
	        
	    	logger.info("Parameters: " + StringUtils.join(config.getParameters().entrySet().toArray(), "\n"));
	    	logger.info("Guacad: Host: " + sockethost + " Port: " + socketport);
	    	
	        // Connect to guacd        
	        GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
	                new InetGuacamoleSocket(sockethost, Integer.parseInt(socketport)),
	                config
	        );

	        // Return a new tunnel which uses the connected socket
	        return new SimpleGuacamoleTunnel(socket);
		}
		else {
			logger.error("Token not found");
			throw new GuacamoleClientException ("Token parameter not found");
		}
    }
		
	private static boolean isNullOrEmpty(String s) {
		if(s != null && s != "")
			return false;
		return true;
	}
}
