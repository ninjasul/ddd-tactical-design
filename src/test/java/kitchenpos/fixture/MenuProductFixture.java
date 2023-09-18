package kitchenpos.fixture;

import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.product.domain.Product;

import static kitchenpos.fixture.ProductFixture.createProduct;

public class MenuProductFixture {
    public static MenuProduct createMenuProductWithDefaultId() {
        return createMenuProduct(1L, createProduct(), 10);
    }

    public static MenuProduct createMenuProductWithDefaultId(final Product product) {
        return createMenuProduct(1L, product, 10);
    }

    public static MenuProduct createMenuProductWithDefaultId(final Product product, final long quantity) {
        return createMenuProduct(1L, product, quantity);
    }

    public static MenuProduct createMenuProduct(final Long seq, final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();

        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());

        return menuProduct;
    }
}