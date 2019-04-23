package de.rudisch.exercices.shoppingcart;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;

public final class Utils {
	private Utils() {
	}

	public static JsonObject readRequest(HttpServletRequest req) throws IOException {
		JsonReader jsonReader = Json.createReader(req.getReader());
		JsonObject jo = jsonReader.readObject();
		jsonReader.close();
		return jo;
	}
}
