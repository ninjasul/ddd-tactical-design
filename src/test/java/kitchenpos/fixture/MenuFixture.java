package kitchenpos.fixture;

import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menugroup.domain.MenuGroup;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;

;

public class MenuFixture {

    public static Menu createMenu() {
        return createMenu(
                null, "치킨", BigDecimal.ONE,
                createMenuGroup(), true, Collections.emptyList()
        );
    }

    public static Menu createMenu(final String name, final BigDecimal price, final MenuGroup menuGroup, final List<MenuProduct> menuProducts) {
        return createMenu(
                null, name, price,
                menuGroup, true, menuProducts
        );
    }

    public static Menu createMenu(final BigDecimal price) {
        return createMenu(
                null, "치킨", price,
                createMenuGroup(), true, Collections.emptyList()
        );
    }

    public static Menu createMenu(final MenuGroup menuGroup, final List<MenuProduct> menuProducts) {
        return createMenu(
                null, "치킨", BigDecimal.TEN,
                menuGroup, true, menuProducts
        );
    }

    public static Menu createMenu(final MenuGroup menuGroup, final List<MenuProduct> menuProducts, UUID menuGroupId) {
        return createMenu(
                null, "치킨", BigDecimal.TEN,
                menuGroup, true, menuProducts, menuGroupId
        );
    }

    public static Menu createMenu(final UUID id, final MenuGroup menuGroup, final boolean displayed, final List<MenuProduct> menuProducts) {
        return createMenu(
                id, "치킨", BigDecimal.TEN,
                menuGroup, displayed, menuProducts
        );
    }

    public static Menu createMenu(final UUID id, final BigDecimal price, final MenuGroup menuGroup, final boolean displayed, final List<MenuProduct> menuProducts) {
        return createMenu(
                id, "치킨", price,
                menuGroup, displayed, menuProducts
        );
    }

    public static Menu createMenu(
            final UUID id, final String name, final BigDecimal price,
            final MenuGroup menuGroup, final List<MenuProduct> menuProducts
    ) {
        return createMenu(id, name, price, menuGroup, true, menuProducts, menuGroup.getId());
    }

    public static Menu createMenu(
            final UUID id, final String name, final BigDecimal price,
            final MenuGroup menuGroup, final boolean displayed, final List<MenuProduct> menuProducts
    ) {
        return createMenu(id, name, price, menuGroup, displayed, menuProducts, menuGroup.getId());
    }

    public static Menu createMenu(
            final UUID id, final String name, final BigDecimal price,
            final MenuGroup menuGroup, final boolean displayed, final List<MenuProduct> menuProducts,
            final UUID menuGroupId
    ) {
        final Menu menu = new Menu();

        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroupId(menuGroupId);

        return menu;
    }
}