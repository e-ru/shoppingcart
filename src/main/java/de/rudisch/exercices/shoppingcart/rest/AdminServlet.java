package de.rudisch.exercices.shoppingcart.rest;

import java.io.IOException;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rudisch.exercices.shoppingcart.Utils;
import de.rudisch.exercices.shoppingcart.persistence.DBManager;
import de.rudisch.exercices.shoppingcart.persistence.PricingRule;

public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PERSITENCE_PATH = "persistence";

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE");
		resp.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		resp.addHeader("Access-Control-Max-Age", "1");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String[] path = req.getPathInfo() != null ? req.getPathInfo().substring(1).split("/") : new String[0];

		String ret;
		if (path.length == 1 && PERSITENCE_PATH.equals(path[0])) {
			resp.setStatus(HttpServletResponse.SC_OK);
			ret = getPricingRules().toString();
		} else if (path.length == 2 && "persistence".equals(path[0])) {
			Optional<PricingRule> opt = getPricingRule(path[1]);
			if (opt.isPresent())
				ret = opt.get().toJson().toString();
			else
				ret = Json.createObjectBuilder().add("error", "invalid request").build().toString();
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			ret = Json.createObjectBuilder().add("error", "invalid request").build().toString();
		}
		resp.getWriter().println(ret);
	}

	private JsonArray getPricingRules() {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for (PricingRule pr : DBManager.trySelectPricingRules()) {
			jab.add(pr.toJson());
		}
		return jab.build();
	}

	private Optional<PricingRule> getPricingRule(String sku) {
		return DBManager.trySelectPricingRules().stream().filter(pr -> sku.equals(pr.getSku())).findFirst();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String[] path = req.getPathInfo() != null ? req.getPathInfo().substring(1).split("/") : new String[0];
		if (path.length == 1 && PERSITENCE_PATH.equals(path[0])) {
			PricingRule pr = addRule(req);
			if (pr != null) {
				resp.setStatus(HttpServletResponse.SC_CREATED);
				resp.getWriter().println(DBManager.trySelectPricingRule(pr.getSku()).toJson().toString());
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				resp.getWriter()
						.println(Json.createObjectBuilder().add("error", "Could not add rule.").build().toString());
			}
		}
	}

	private PricingRule addRule(HttpServletRequest req) throws IOException {
		PricingRule pricingRule = null;
		JsonObject jo = Utils.readRequest(req);
		if (jo.containsKey("sku") && jo.containsKey("unitPrice") && !DBManager.trySelectPricingRules().stream()
				.filter(pr -> jo.getString("sku").equals(pr.getSku())).findFirst().isPresent()) {
			if (jo.containsKey("specialCount") && jo.containsKey("specialPrice")) {
				pricingRule = new PricingRule(jo.getString("sku"), jo.getInt("unitPrice"), jo.getInt("specialCount"),
						jo.getInt("specialPrice"));
			} else {
				pricingRule = new PricingRule(jo.getString("sku"), jo.getInt("unitPrice"));
			}
			DBManager.tryInsertPricingRule(pricingRule);
		}
		return pricingRule;
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String[] path = req.getPathInfo() != null ? req.getPathInfo().substring(1).split("/") : new String[0];

		if (path.length == 2 && PERSITENCE_PATH.equals(path[0])) {
			PricingRule pr = updateRule(path[1], req);
			if (pr != null) {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().println(pr.toJson().toString());
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				resp.getWriter()
						.println(Json.createObjectBuilder().add("error", "Could not update rule.").build().toString());
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.getWriter()
					.println(Json.createObjectBuilder().add("error", "Could not update rule.").build().toString());
		}
	}

	private PricingRule updateRule(String sku, HttpServletRequest req) throws IOException {
		JsonObject jo = Utils.readRequest(req);

		// String sku = jo.getString("sku", ShoppingcartServlet.NOT_INCLUDED);
		int unitPrice = jo.getInt("unitPrice", -1);
		int specialCount = jo.getInt("specialCount", -1);
		int specialPrice = jo.getInt("specialPrice", -1);

		PricingRule pr = DBManager.trySelectPricingRule(sku);

		if (pr != null) {
			if (unitPrice != -1)
				pr.setUnitPrice(unitPrice);
			if (specialCount != -1)
				pr.setSpecialCount(specialCount);
			if (specialPrice != -1)
				pr.setSpecialPrice(specialPrice);
			DBManager.tryUpdatePricingRule(pr);
		}

		return pr;
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String[] path = req.getPathInfo() != null ? req.getPathInfo().substring(1).split("/") : new String[0];

		if (path.length == 2 && PERSITENCE_PATH.equals(path[0])) {
			if (DBManager.tryDeletePricingRule(path[1])) {
				resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				resp.getWriter()
						.println(Json.createObjectBuilder().add("error", "Could not delete rule.").build().toString());

			}
		}
	}

}
