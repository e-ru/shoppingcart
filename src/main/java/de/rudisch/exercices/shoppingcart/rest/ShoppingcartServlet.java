package de.rudisch.exercices.shoppingcart.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rudisch.exercices.shoppingcart.Utils;
import de.rudisch.exercices.shoppingcart.business.Checkout;
import de.rudisch.exercices.shoppingcart.persistence.DBManager;

public class ShoppingcartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String NOT_INCLUDED = "not-included";

	private static final List<String> items = new ArrayList<>();

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

		JsonObject jo;
		if (path.length == 1 && "total".equals(path[0])) {
			resp.setStatus(HttpServletResponse.SC_OK);
			jo = calcTotal();
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			jo = Json.createObjectBuilder().add("error", "Invalid path").build();
		}
		resp.getWriter().println(jo.toString());

	}

	private JsonObject calcTotal() {
		var co = new Checkout(DBManager.trySelectPricingRules());
		items.forEach(sku -> co.scan(sku));
		return Json.createObjectBuilder().add("total", co.total()).build();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");

		if (addToCart(req)) {
			resp.setStatus(HttpServletResponse.SC_CREATED);
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			JsonObject jo = Json.createObjectBuilder().add("error", "Invalid sku").build();
			resp.getWriter().println(jo.toString());
		}
	}

	private boolean addToCart(HttpServletRequest req) throws IOException {
		JsonObject jo = Utils.readRequest(req);
		boolean valid = DBManager.trySelectPricingRules().stream()
				.filter(pr -> jo.getString("sku", NOT_INCLUDED).equals(pr.getSku())).findFirst().isPresent();
		if (valid)
			items.add(jo.getString("sku"));
		return valid;
	}

}
