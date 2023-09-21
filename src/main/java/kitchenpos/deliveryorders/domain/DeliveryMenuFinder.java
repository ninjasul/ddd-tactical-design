package kitchenpos.deliveryorders.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import kitchenpos.common.DomainService;
import kitchenpos.eatinorders.domain.Order;
import kitchenpos.eatinorders.domain.OrderLineItem;
import kitchenpos.menus.domain.tobe.domain.ToBeMenu;
import kitchenpos.menus.domain.tobe.domain.ToBeMenuRepository;
import kitchenpos.menus.domain.tobe.domain.ToBeMenus;

@DomainService
public class DeliveryMenuFinder {
    private final ToBeMenuRepository menuRepository;

    public DeliveryMenuFinder(ToBeMenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public DeliveryOrderLineItems orderLineItemsGenerator(final Order request) {
        validationOfNull(request);
        ToBeMenus menu = findMenu(request);
        List<DeliveryOrderLineItem> orderLineItem = request.getOrderLineItems().stream()
            .map(it -> {
                if (menu.hasHiddenMenu()) {
                    throw new IllegalStateException("숨겨진 메뉴는 주문할 수 없습니다.");
                }
                if (menu.isNotMatchByMenuAndPrice(it.getMenuId(), it.getPrice())) {
                    throw new IllegalStateException("주문한 메뉴의 가격은 실제 메뉴 가격과 일치해야 합니다다.");
                }
                return new DeliveryOrderLineItem(
                    new DeliveryOrderMenu(it.getMenuId(), DeliveryOrderMenuPrice.of(it.getPrice())),
                    DeliveryOrderQuantity.of(it.getQuantity()));
            })
            .collect(Collectors.toList());
        return new DeliveryOrderLineItems(orderLineItem);
    }

    private void validationOfNull(final Order request) {
        final List<OrderLineItem> orderLineItemRequests = request.getOrderLineItems();
        if (Objects.isNull(orderLineItemRequests) || orderLineItemRequests.isEmpty()) {
            throw new IllegalArgumentException("주문내역을 찾을 수 없습니다.");
        }
    }

    private ToBeMenus findMenu(final Order request) {
        List<ToBeMenu> menuList = menuRepository.findAllByIdIn(request.getOrderLineItems().stream()
            .map(OrderLineItem::getMenuId)
            .collect(Collectors.toList()));
        return new ToBeMenus(menuList);
    }
}