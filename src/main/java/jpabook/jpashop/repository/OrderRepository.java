package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    // 주문 저장
    public void save(Order order){
        em.persist(order);
    }

    // 주문 단건 조회
    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    // 주문 리스트 조회(검색조건 - 동적쿼리)
    public List<Order> findAllByString(OrderSearch orderSearch) {
//        return em.createQuery("select o from Order o join o.member m where o.status = :status and m.name = :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                //.setFirstResult(pageIndex)    // 페이징할 경우 현재 페이지번호
//                .setMaxResults(1000)    // 최대 1000건
//                .getResultList();
        // 위 주석처럼 하면 조건이 하나라도 없을 때 에러발생 -> 동적쿼리 만들어줘야한다.

        String jpql = "select o from Order o join o.member m";
        return em.createQuery(jpql,Order.class)
                .setMaxResults(1000)    // 최대 1000건
                .getResultList();
    }
}
