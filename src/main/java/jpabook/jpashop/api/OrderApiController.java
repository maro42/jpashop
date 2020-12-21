package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.yaml.snakeyaml.nodes.NodeId.mapping;

/**
 * OneToMany 관계에서 컬렉션 최적화
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * 1. 엔티티 직접 반환
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(item -> item.getItem().getName());
        }

        return  all;
    }

    /**
     * 2. 엔티티를 DTO로 변환
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // 내부에 있는 엔티티도 노출이 되면 안된다. -> 내부 엔티티도 dto를 만들어서 뿌려주자
        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(toList());

        return result;
    }

    /**
     * 3-1. DTO변환 : 페치조인 최적화
     * 주의 : xToMany관계에서 컬렉션 페치조인은 페이징이 안됨.
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(toList());

        return result;
    }

    /**
     * 3-2. DTO 변환 : 페이징 처리
     */
    @GetMapping("/api/v3/orders/page")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {

        // 1. xToOne 관계는 fetch join으로 다 가져온다.
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        // 2. application.yml 파일에 default_batch_fetch_size 설정해서 inquery 쓴다.

        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(toList());

        return result;
    }

    /**
     * 4. JPA에서 DTO직접 조회
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    /**
     * 5. JPA에서 DTO직접 조회 - 컬렉션 최적화
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findOrderQueryDtosOptimization();
    }

    /**
     * 6. JPA에서 DTO직접 조회 - 플랫데이터 최적화
     */
/*    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findOrderQueryDtosFlat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }*/


    //============ inner class(dto) ============
    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                              .map(orderItem -> new OrderItemDto(orderItem))
                              .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;    // 상품명
        private int orderPrice;     // 주문가격
        private int count;          // 주문수량

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
