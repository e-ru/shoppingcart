package de.rudisch.exercices.shoppingcart.business;

public class CartItem {

	private String sku;
	private int unitPrice;
	private int specialCount;
	private int specialPrice;
	private long count;

	public CartItem(String sku, int unitPrice, int specialCount, int specialPrice, long count) {
		this.sku = sku;
		this.unitPrice = unitPrice;
		this.specialCount = specialCount;
		this.specialPrice = specialPrice;
		this.count = count;
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

	public void setUnitPrice(int price) {
		this.unitPrice = price;
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

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
