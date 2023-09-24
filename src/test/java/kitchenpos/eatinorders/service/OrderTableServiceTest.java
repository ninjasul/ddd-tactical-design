package kitchenpos.eatinorders.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kitchenpos.eatinorders.application.OrderTableService;
import kitchenpos.eatinorders.domain.OrderRepository;
import kitchenpos.eatinorders.domain.OrderTable;
import kitchenpos.eatinorders.domain.OrderTableRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Mock
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void init() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Test
    void 주문테이블_생성_실패__이름이_null() {
        OrderTable request = OrderTableFixture.builder()
                .name(null)
                .build();

        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문테이블_생성_실패__이름이_비어있음() {
        OrderTable request = OrderTableFixture.builder()
                .name("")
                .build();

        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문테이블_정리_실패__해당_주문테이블에_완료되지_않은_주문이_존재() {
        OrderTable orderTable = orderTableService.create(OrderTableFixture.builder().build());
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문테이블_손님수_변경_실패__손님수가_음수() {
        OrderTable orderTable = orderTableService.create(OrderTableFixture.builder().build());
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문테이블_손님수_변경_실패__주문테이블이_착석상태가_아님() {
        OrderTable createRequest = OrderTableFixture.builder()
                .occupied(false)
                .build();
        OrderTable orderTable = orderTableService.create(createRequest);
        OrderTable request = new OrderTable();

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalStateException.class);
    }
}