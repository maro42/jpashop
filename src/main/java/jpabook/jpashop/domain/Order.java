package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;    // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 : [ORDER, CANCEL] enum타입


    // 연관관계 편의 메소드 (양뱡향 연관관계일 때 양쪽에 일일이 객체 넣어주기보다는 한번에 넣을 수 있게 처리)------------------------------
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // -----------------------------------

    // -------- 생성 메소드( 복잡한 로직에 있으면 좋음) ---
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
       for(OrderItem orderItem : orderItems){
           order.addOrderItem((orderItem));
       }

       order.setStatus(OrderStatus.ORDER);
       order.setOrderDate(LocalDateTime.now());
       return order;
    }

    // -------비즈니스 로직 -------
    /**
     * 주문 취소
     */
    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다");
        }

        this.setStatus(OrderStatus.CANCEL);
        // 주문에 여러 주문상품이 존재하기 때문에 돌면서 각각의 상품 주문 취소
        for(OrderItem orderItem : this.orderItems){
            orderItem.cancel();
        }
    }

    // ------- 조회 로직 -------

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){

//       int totalPrice = 0;
//       for(OrderItem orderItem : orderItems){
//           totalPrice += orderItem.getTotalPrice();
//       }
//
//       return totalPrice;

        // 위랑 같은거임 java8문법
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
