package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/*@Controller @ResponseBody*/
@RestController // 위 2개 합친 것
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원 생성 1
     * @param member
     * @return
     */
    @PostMapping("/api/v1/members")
    //  @RequestBody : json으로 들어온 값을 Member객체로 자동 변환해준다.
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원생성 2
     * @param request
     * @return
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMember2(@RequestBody @Valid CreateMemberRequest request){

        // 파라미터랑 엔티티랑 이렇게 중간에서 매핑해주면 api 스펙이 바뀔일이 없다.
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 수정
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                              @RequestBody @Valid UpdateMemberResponse request){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOneMember(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * 회원 조회 1
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        // 안좋은 방법 - 엔티티를 직접 반환하면 만약 주문정보가 있다고하면 주문정보까지 다 노출된다.
        //           - api 스펙이 변경됨
        return memberService.findMembers();
    }

    /**
     * 회원 조회 2
     */
    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> members = memberService.findMembers();
        List<MemberDTO> collect = members.stream()
                .map(m -> new MemberDTO(m.getId(), m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    //--------------------------------------------- dto innerClass

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
