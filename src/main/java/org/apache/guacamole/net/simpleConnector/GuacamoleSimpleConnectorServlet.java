package org.apache.guacamole.net.simpleConnector;

import javax.servlet.http.HttpServletRequest;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleSocket;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.apache.guacamole.servlet.GuacamoleHTTPTunnelServlet;

public class GuacamoleSimpleConnectorServlet
    extends GuacamoleHTTPTunnelServlet {

	private static final long serialVersionUID = 1L;
			
	@Override
    protected GuacamoleTunnel doConnect(HttpServletRequest request)
        throws GuacamoleException {

		// Example query string
		// ?sockethost=172.17.0.3&sockerport=4822&hostname=192.168.0.153&port=3389&username=username&domain=domain&password=password
    	
		// Create configuration from params
		GuacamoleConfiguration config = new GuacamoleConfiguration();
        config.setProtocol("rdp");
        config.setParameter("hostname", request.getParameter("hostname"));
        config.setParameter("port", request.getParameter("port"));
        config.setParameter("username", request.getParameter("username"));
        config.setParameter("domain", request.getParameter("domain"));
        config.setParameter("password", request.getParameter("password"));
        config.setParameter("security", "any");
        config.setParameter("ignore-cert", "true");
        

        // Connect to guacd        
        GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
                new InetGuacamoleSocket(request.getParameter("sockethost"), Integer.parseInt(request.getParameter("sockerport"))),
                config
        );

        // Return a new tunnel which uses the connected socket
        return new SimpleGuacamoleTunnel(socket);
    }
}
