package kitchenpos.eatinorders.application;

import static kitchenpos.eatinorders.OrderFixture.createTakeOutOrderCreateRequestBuilder;
import static kitchenpos.eatinorders.domain.OrderStatus.ACCEPTED;
import static kitchenpos.eatinorders.domain.OrderType.TAKEOUT;
import static kitchenpos.menus.MenuFixture.menu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import kitchenpos.eatinorders.OrderFixture;
import kitchenpos.eatinorders.domain.Order;
import kitchenpos.eatinorders.domain.OrderRepository;
import kitchenpos.eatinorders.domain.OrderStatus;
import kitchenpos.eatinorders.domain.OrderTableRepository;
import kitchenpos.eatinorders.domain.OrderType;
import kitchenpos.menus.application.InMemoryMenuRepository;
import kitchenpos.menus.domain.Menu;
import kitchenpos.menus.domain.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TakeOutOrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private FakeKitchenridersClient kitchenridersClient;
    private OrderService orderService;
    private Menu menu;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        kitchenridersClient = new FakeKitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
        menu = menuRepository.save(menu());
    }

    @DisplayName("포장주문을 등록할수 있다")
    @Test
    void test1() {
        //given
        Order createRequest = createTakeOutOrderCreateRequestBuilder()
            .menu(menu, 1L, menu.getPrice())
            .type(TAKEOUT)
            .build();

        //when
        Order result = orderService.create(createRequest);

        //then
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("저장된 메뉴의 가격과 주문한 메뉴의 가격이 다를수 없다")
    @Test
    void test2() {
        //given
        Order createRequest = OrderFixture.createTakeOutOrderCreateRequestBuilder()
            .menu(menu, 1L, menu.getPrice().add(BigDecimal.ONE))
            .type(OrderType.TAKEOUT)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderService.create(createRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("'포장주문'이라면 메뉴 별로 0개 이상 주문해야 한다")
    @Test
    void test3() {
        //given
        long quantity = -1L;
        Order createRequest = OrderFixture.createTakeOutOrderCreateRequestBuilder()
            .menu(menu, quantity, menu.getPrice())
            .type(OrderType.TAKEOUT)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderService.create(createRequest)
        ).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("포장 주문 접수시 '접수 완료'상태로 변경되어야 한다")
    @Test
    void test4() {
        //given
        Order order = orderService.create(
            OrderFixture.createTakeOutOrderCreateRequestBuilder()
                .menu(menu, 1L, menu.getPrice())
                .type(OrderType.TAKEOUT)
                .build()
        );
        //when
        Order result = orderService.accept(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
    }

    @DisplayName("포장 주문 서빙시 '서빙중' 상태로 변경되어야 한다")
    @Test
    void test5() {
        //given
        Order order = orderService.create(
            OrderFixture.createTakeOutOrderCreateRequestBuilder()
                .menu(menu, 1L, menu.getPrice())
                .type(OrderType.TAKEOUT)
                .build()
        );
        orderService.accept(order.getId());

        //when
        Order result = orderService.serve(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("포장 주문을 완료처리 할수 있다")
    @Test
    void test6() {
        //given
        Order order = orderService.create(
            OrderFixture.createTakeOutOrderCreateRequestBuilder()
                .menu(menu, 1L, menu.getPrice())
                .type(OrderType.TAKEOUT)
                .build()
        );
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        //when
        Order result = orderService.complete(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}