package de.rudisch.exercices.shoppingcart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rudisch.exercices.shoppingcart.persistence.DBManager;

public class Main {
	private static final Logger LOG = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		LOG.info("Starting...");
		DBManager.tryCreateAndFillTable();
		new Jetty().start();
	}
}
