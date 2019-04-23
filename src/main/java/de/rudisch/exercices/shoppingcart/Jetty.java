package de.rudisch.exercices.shoppingcart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import de.rudisch.exercices.shoppingcart.rest.AdminServlet;
import de.rudisch.exercices.shoppingcart.rest.ShoppingcartServlet;

public class Jetty {
	private static final Logger LOG = LogManager.getLogger(Jetty.class);

	private Server server;

	public void start() {
		LOG.info("Try starting jetty server...");
		server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setHost("127.0.0.1");
		connector.setPort(8090);
		connector.setIdleTimeout(30000);
		server.setConnectors(new Connector[] { connector });

		ServletHandler servletHandler = new ServletHandler();
		server.setHandler(servletHandler);

		servletHandler.addServletWithMapping(ShoppingcartServlet.class, "/shoppingcart/*");
		servletHandler.addServletWithMapping(AdminServlet.class, "/admin/*");

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			LOG.error("Could not start jetty server.");
		}
	}

	public void stop() throws Exception {
		server.stop();
	}
}
