package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
//@AllArgsConstructor // 클래스의 모든 필드를 가지고 생성자를 만들어주는 어노테이션
@RequiredArgsConstructor    // final이 있는 필드만 가지고 생성자 만들어줌
public class MemberService {

    // 이렇게 쓰면 repository를 못바꾸니까
//    @Autowired
//    private MemberRepository memberRepository;

    /**
     * 아래 같이 생성자 injection을 사용한다. : 테스트코드 작성 시 다른 레파지토리를 넣어서 테스트할 수 있음
     * 참고 : setter injection은 실제 어플리케이션이 돌아갈 때 누군가 바꿀 위험이 있어 생성자 injection을 사용하면 안전
     */
    private final MemberRepository memberRepository;

//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }


    /**
     * 회원가입(등록)
     */
    @Transactional  // 클래스에 있는 읽기전용 트랜잭션보다 우선순위가 높아서 그냥 트랜잭션으로 먹힘
    public Long join(Member member) {

        // 중복 회원 검증
        validateDuplicateMember(member);    // 멀티쓰레드나 WAS 여러개 있을 때 동시에 호출하면 중복검증이 제대로 안되고 같은 name이 들어갈 수 있음 : DB에 멤버이름(name)을 unique 제약조건 걸어주자.
        memberRepository.save(member);
        return member.getId();  // 무엇이 저장되었는지 확인하기 위해
    }

    // 중복 회원 검증 메소드
    private void validateDuplicateMember(Member member) {

        List<Member> findMembers = memberRepository.findByName(member.getName());
        // 조회 결과가 있으면 중복 : EXCEPTION
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    /**
     * 회원 리스트 조회(회원 전체 조회)
     * @return
     */
//    @Transactional(readOnly = true) // 읽기전용 트랜잭션 - 메모리 덜먹어서 성능 좋아짐
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 한명 조회(조회)
     */
//    @Transactional(readOnly = true)
    public Member findOneMember(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
