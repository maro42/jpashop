package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    // 부모
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // 자식
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();


    // 양방향 편의 관계 메소드(내 카테고리가 등록되면 부모카테고리에도 등록되어야한다.)
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }


}
