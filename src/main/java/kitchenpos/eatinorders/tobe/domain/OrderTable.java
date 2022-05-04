package kitchenpos.eatinorders.tobe.domain;

import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name = "order_table")
@Entity
public class OrderTable {

    private static final int EMPTY_TABLE_GUESTS = 0;
    private static final boolean DEFAULT_TABLE_STATUS = true;

    @Id
    @Column(name = "id", columnDefinition = "varbinary(16)")
    @GeneratedValue
    private UUID id;

    @Embedded
    private OrderTableName name;

    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    @Column(name = "empty", nullable = false)
    private boolean empty;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(
            name = "order_table_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_orders_to_order_table")
    )
    private List<EatInOrder> eatInOrders;

    protected OrderTable() { }

    private OrderTable(OrderTableName name, int numberOfGuests, boolean empty) {
        this.name = name;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public static OrderTable create(OrderTableName name) {
        return new OrderTable(name, EMPTY_TABLE_GUESTS, DEFAULT_TABLE_STATUS);
    }

    public static OrderTable create(String name) {
        return new OrderTable(OrderTableName.create(name), EMPTY_TABLE_GUESTS, DEFAULT_TABLE_STATUS);
    }

    public void sit() {
        if (!empty) {
            throw new IllegalStateException("이미 사용중인 테이블입니다.");
        }

        empty = false;
    }

    public void changeNumberOfGuests(int numberOfGuests) {
        if (numberOfGuests < EMPTY_TABLE_GUESTS) {
            throw new IllegalArgumentException("손님 수는 0 이상의 정수여야 합니다.");
        }

        if (empty) {
            throw new IllegalStateException("비어있는 테이블의 손님 수는 변경할 수 없습니다.");
        }

        this.numberOfGuests = numberOfGuests;
    }

    public void clear() {
        numberOfGuests = 0;
        empty = true;
    }

    public boolean isCompletedAllOrders() {
        return eatInOrders.stream().allMatch(EatInOrder::isCompletedOrder);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name.value();
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }

    public List<EatInOrder> getEatInOrders() {
        return eatInOrders;
    }
}