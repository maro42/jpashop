package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // 디비에 테이블 생성될 때 상속관계에 있는 테이블을 어떻게 처리할 것인가 (여긴 싱글테이블 전략)
@DiscriminatorColumn(name = "dtype")    // 해당 컬럼이 어떤 상속관계에 있는 테이블인지 타입을 알려주는 정보
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // ------- 비즈니스 로직 ---------

    /**
     * stock(재고) 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;

        if(restStock < 0) { // 재고수량이 0보다 작으면 예외처리
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
    // -----------------------------
}
