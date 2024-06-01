package kitchenpos.orders.tobe.application;

import static kitchenpos.orders.tobe.domain.Order.*;
import static kitchenpos.orders.tobe.domain.OrderTable.*;
import static kitchenpos.orders.tobe.domain.OrderType.*;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.orders.tobe.application.adapter.MenuServiceAdapter;
import kitchenpos.orders.tobe.application.dto.OrderCreationRequest;
import kitchenpos.orders.tobe.domain.KitchenridersClient;
import kitchenpos.orders.tobe.domain.Order;
import kitchenpos.orders.tobe.domain.OrderLineItems;
import kitchenpos.orders.tobe.domain.OrderRepository;
import kitchenpos.orders.tobe.domain.OrderStatus;
import kitchenpos.orders.tobe.domain.OrderTable;
import kitchenpos.orders.tobe.domain.OrderTableRepository;
import kitchenpos.orders.tobe.domain.OrderType;
import kitchenpos.orders.tobe.domain.factory.OrderFactory;
import kitchenpos.orders.tobe.domain.factory.OrderFactoryProvider;

@Service
public class EatInOrderService extends OrderService {

    public EatInOrderService(
        OrderRepository orderRepository,
        MenuServiceAdapter menuServiceAdapter,
        OrderTableRepository orderTableRepository,
        KitchenridersClient kitchenridersClient,
        OrderFactoryProvider orderFactoryProvider
    ) {
        super(orderRepository, menuServiceAdapter, orderTableRepository, kitchenridersClient, orderFactoryProvider);
    }

    @Override
    @Transactional
    public Order create(final OrderCreationRequest request) {
        if (request.type() != OrderType.EAT_IN) {
            throw new IllegalArgumentException(WRONG_ORDER_TYPE_ERROR);
        }

        OrderLineItems orderLineItems = createOrderLineItems(request);

        OrderTable orderTable = orderTableRepository.findById(request.orderTableId())
            .orElseThrow(() -> new NoSuchElementException(ORDER_TABLE_NOT_FOUND_ERROR));

        OrderFactory orderFactory = orderFactoryProvider.getFactory(request.type());
        Order order = orderFactory.createOrder(EAT_IN, orderLineItems, orderTable, request.deliveryAddress());

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order complete(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(NoSuchElementException::new);

        if (order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalStateException(INVALID_ORDER_STATUS_ERROR);
        }

        Order completedOrder = order.completed();

        if (!orderRepository.existsByOrderTableAndStatusNot(order.getOrderTable(), OrderStatus.COMPLETED)) {
            order.getOrderTable().used(false);
        }

        return completedOrder;
    }
}