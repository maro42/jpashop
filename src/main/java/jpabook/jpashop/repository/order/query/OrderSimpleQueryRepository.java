package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    // api 스팩이 들어와서 그렇게 좋지는 않음ㅎㅎ
    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                "from Order o join o.member m join o.delivery d", SimpleOrderQueryDto.class)
                .getResultList();
    }
}
