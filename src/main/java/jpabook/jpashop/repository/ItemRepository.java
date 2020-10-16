package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    // 상품등록(저장)
    public void save(Item item) {
        if(item.getId() == null) {  // 완전 처음 등록 경우 영속성 컨텍스트에 없으니까 새로 저장
            em.persist(item);
        }else{  // 기존 정보가 있다.
            em.merge(item); // update랑 비슷.
        }
    }

    // 상품 단건 조회
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    // 상품 리스트 조회
    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
