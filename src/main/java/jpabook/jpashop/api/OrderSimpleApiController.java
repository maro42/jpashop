package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderSimpleQueryRepository;
import jpabook.jpashop.repository.order.query.SimpleOrderQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne) 관계
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    // dto반환 용 repository
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple_orders")
    public List<Order> orderV1() {

        /**
         * 엔티티를 직접 반환
         * 무한루프 돈다. order와 member가 양방향연관관계로 매핑되어있어서 계속 서로를 호출함(사용X)
         */
       List<Order> all = orderRepository.findAllByString(new OrderSearch());
       for(Order order : all) {
           order.getMember().getName(); // member의 name을 불러오는 순간 Lazy 강제 초기화된다. 그래서 멤버를 디비에서 다시 가져온다.
           order.getDelivery().getAddress();
       }
        return all;
    }

    /**
     * 엔티티를 dto로 반환
     */
    @GetMapping("/api/v2/simple_orders")
    public List<SimpleOrderDto> ordersV2() {
        /**
         * N+1문제 발생
         * 쿼리 날릴 때 order 1번 + member n번 + delevery n번 발생
         */
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                                            .map(order -> new SimpleOrderDto(order))    // simpleOrderDto의 생성자를 map으로 변환해준다.
                                            .collect(Collectors.toList());              // 변환된 맴을 List로 변환해준다.
        return result;
    }

    /**
     * fetch join 사용
     * N+1문제 해결 : 각 테이블들을 조인해서 쿼리를 한번만 날림
     */
    @GetMapping("/api/v3/simple_orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(order -> new SimpleOrderDto(order))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * JPA에서 DTO로 바로 조회
     */
    @GetMapping("/api/v4/simple_orders")
    public List<SimpleOrderQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }

    //============ inner class(dto) ============
    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화 : 영속성 컨텍스트가 id를 가지고 member의 name을 찾는다. 영속성컨텍스트에 없으면 db에서 조회해온다.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
