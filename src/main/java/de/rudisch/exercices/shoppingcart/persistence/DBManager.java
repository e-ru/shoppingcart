package de.rudisch.exercices.shoppingcart.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBManager {

	private static final Logger LOG = LogManager.getLogger(DBManager.class);

	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_CONNECTION = "jdbc:h2:mem:shoppingcart;DB_CLOSE_DELAY=-1";
	private static final String DB_USER = "";
	private static final String DB_PASSWORD = "";

	public static void tryCreateAndFillTable() {
		LOG.info("Try to create shoppingcart table...");
		Connection connection = getDBConnection();

		PreparedStatement preparedStatement = null;
		String create = "CREATE TABLE pricing(id int auto_increment primary key, sku varchar(4),"
				+ " unit_price int, special_count int, special_price int)";

		try {
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(create);
			preparedStatement.executeUpdate();
			preparedStatement.close();

			connection.commit();
			LOG.info("Successfully create shoppingcart table.");

			tryInsertPricingRule(new PricingRule("A", 40, 3, 100));
			tryInsertPricingRule(new PricingRule("B", 50, 2, 80));
			tryInsertPricingRule(new PricingRule("C", 25));
			tryInsertPricingRule(new PricingRule("D", 20));
		} catch (SQLException e) {
			LOG.error("Could not create table!", e);
		}
	}

	public static void tryInsertPricingRule(PricingRule pricingRule) {
		Connection connection = getDBConnection();

		PreparedStatement preparedStatement = null;
		String insert = "INSERT INTO pricing(sku, unit_price, special_count, special_price) VALUES(?, ?, ?, ?)";

		try {
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(insert);
			preparedStatement.setString(1, pricingRule.getSku());
			preparedStatement.setInt(2, pricingRule.getUnitPrice());
			preparedStatement.setInt(3, pricingRule.getSpecialCount());
			preparedStatement.setInt(4, pricingRule.getSpecialPrice());
			preparedStatement.executeUpdate();
			preparedStatement.close();

			connection.commit();
			LOG.info("Successfully insert rule to table: " + pricingRule.toString());
		} catch (SQLException e) {
			LOG.error("Could not insert rule to table!", e);
		}
	}

	public static void tryUpdatePricingRule(PricingRule pricingRule) {
		Connection connection = getDBConnection();

		PreparedStatement preparedStatement = null;
		String update = "UPDATE pricing SET unit_price = ?, special_count = ?, special_price = ? WHERE sku = ?";

		try {
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(update);
			preparedStatement.setInt(1, pricingRule.getUnitPrice());
			preparedStatement.setInt(2, pricingRule.getSpecialCount());
			preparedStatement.setInt(3, pricingRule.getSpecialPrice());
			preparedStatement.setString(4, pricingRule.getSku());
			preparedStatement.executeUpdate();
			preparedStatement.close();

			connection.commit();
			LOG.info("Successfully update rule: " + pricingRule.toString());
		} catch (SQLException e) {
			LOG.error("Could not update rule!", e);
		}
	}

	public static List<PricingRule> trySelectPricingRules() {
		List<PricingRule> pricingRules = new ArrayList<>();
		Connection connection = getDBConnection();
		PreparedStatement preparedStatement = null;
		String select = "SELECT * FROM pricing";

		try {
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(select);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				pricingRules.add(new PricingRule(rs.getInt("id"), rs.getString("sku"), rs.getInt("unit_price"),
						rs.getInt("special_count"), rs.getInt("special_price")));
			}
			preparedStatement.close();

			connection.commit();
		} catch (SQLException e) {
			LOG.error("Could not select rule!", e);
		}
		return pricingRules;
	}

	public static PricingRule trySelectPricingRule(String sku) {
		PricingRule pricingRule = null;
		Connection connection = getDBConnection();
		PreparedStatement preparedStatement = null;
		String select = "SELECT * FROM pricing WHERE sku = ?";

		try {
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(select);
			preparedStatement.setString(1, sku);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				pricingRule = new PricingRule(rs.getInt("id"), rs.getString("sku"), rs.getInt("unit_price"),
						rs.getInt("special_count"), rs.getInt("special_price"));
			}
			preparedStatement.close();

			connection.commit();
			LOG.info("Successfully select rule: " + sku);
		} catch (SQLException e) {
			LOG.error("Could not select rule!", e);
		}
		return pricingRule;
	}

	public static boolean tryDeletePricingRule(String sku) {
		Connection connection = getDBConnection();
		PreparedStatement preparedStatement = null;
		String delete = "DELETE FROM pricing WHERE sku = ?";

		try {
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(delete);
			preparedStatement.setString(1, sku);
			preparedStatement.executeUpdate();

			preparedStatement.close();

			connection.commit();
			LOG.info("Successfully delete rule: " + sku);
			return true;
		} catch (SQLException e) {
			LOG.error("Could not delete rule!", e);
			return false;
		}
	}

	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}

}
