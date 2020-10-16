package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional  // 테스트를 위해 실행했다가 롤백시킴
public class MemberServiceTest {

    // 회원 기능 테스트

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    // 1. 회원가입을 성공해야한다.
    @Test
//    @Rollback(false)
    public void 회원가입() throws Exception {

        // given : 주어진 것
        Member member = new Member();
        member.setName("minju");

        // when : 실행할 것
        Long savedId = memberService.join(member);

        //then : 결과
        assertEquals(member, memberRepository.findOne(savedId));

    }

    // 2. 회원가입 시 같은 이름이 있으면 예외가 발생해야한다.
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {

        // given
        Member member1 = new Member();
        member1.setName("minju1");

        Member member2 = new Member();
        member2.setName("minju1");

        // when
        memberService.join(member1);
        memberService.join(member2);
//        try{
//            memberService.join(member2);    // 예외가 발생해야함
//        }catch (IllegalStateException e) {
//            return;
//        }

        // then
        fail("예외가 발생해야 한다.");

    }

}