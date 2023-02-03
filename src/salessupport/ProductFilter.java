package salessupport;

import database.ProductVariation;

@FunctionalInterface
public interface ProductFilter {
    boolean find(ProductVariation p, String q);
}
