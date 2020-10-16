package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A")   // DB 싱글테이블 시 구분하는 dtype 컬럼에 들어갈 정보
@Getter @Setter
public class Album extends Item {

    private String artist;
    private String etc;
}
