package de.rudisch.exercices.shoppingcart.persistence;

import javax.json.Json;
import javax.json.JsonObject;

public class PricingRule {
	private int id;
	private String sku;
	private int unitPrice;
	private int specialCount;
	private int specialPrice;

	public PricingRule(int id, String sku, int unitPrice, int specialCount, int specialPrice) {
		this(sku, unitPrice, specialCount, specialPrice);
		this.id = id;
	}

	public PricingRule(String sku, int unitPrice, int specialCount, int specialPrice) {
		this(sku, unitPrice);
		this.specialCount = specialCount;
		this.specialPrice = specialPrice;
	}

	public PricingRule(int id, String sku, int unitPrice) {
		this(sku, unitPrice);
		this.id = id;
	}

	public PricingRule(String sku, int unitPrice) {
		this.sku = sku;
		this.unitPrice = unitPrice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}

	public int getSpecialCount() {
		return specialCount;
	}

	public void setSpecialCount(int specialCount) {
		this.specialCount = specialCount;
	}

	public int getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(int specialPrice) {
		this.specialPrice = specialPrice;
	}

	@Override
	public String toString() {
		return "PricingRule [id=" + id + ", sku=" + sku + ", unitPrice=" + unitPrice + ", specialCount=" + specialCount
				+ ", specialPrice=" + specialPrice + "]";
	}

	public JsonObject toJson() {
		return Json.createObjectBuilder().add("id", id).add("sku", sku).add("unitPrice", unitPrice)
				.add("specialCount", specialCount).add("specialPrice", specialPrice).build();
	}
}
