package de.rudisch.exercices.shoppingcart.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.rudisch.exercices.shoppingcart.persistence.PricingRule;

public class Checkout {

	private Map<String, CartItem> items;
	private Map<String, PricingRule> pricingRules;

	public Checkout(List<PricingRule> pricingRules) {
		this.pricingRules = pricingRules.stream().collect(Collectors.toMap(PricingRule::getSku, pr -> pr));
		this.items = new HashMap<>();
	}

	public void scan(String sku) {
		if (items.containsKey(sku)) {
			items.get(sku).setCount(items.get(sku).getCount() + 1);
		} else {
			PricingRule pr = pricingRules.get(sku);
			if (pr != null)
				items.put(sku, new CartItem(sku, pr.getUnitPrice(), pr.getSpecialCount(), pr.getSpecialPrice(), 1L));
		}
	}

	public long total() {
		return items.values().stream().mapToLong(
				item -> calc(item.getCount(), item.getUnitPrice(), item.getSpecialCount(), item.getSpecialPrice()))
				.sum();
	}

	private long calc(long count, int unitPrice, int specialCount, int specialPrice) {
		return specialCount > 0 && specialPrice > 0
				? count / specialCount * specialPrice + count % specialCount * unitPrice
				: count * unitPrice;
	}
}
