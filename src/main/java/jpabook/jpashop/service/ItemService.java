package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // 상품 등록
    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    // 상품 리스트 조회
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    // 상품 단건 조회회
   public Item findItem(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
