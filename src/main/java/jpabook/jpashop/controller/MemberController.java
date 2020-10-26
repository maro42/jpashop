package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원등록 페이지 이동
     */
    @GetMapping("/members/new")
    public String createForm(Model model){

        // 화면에 객체?를 같이 보낼 수 있다.
        model.addAttribute("memberForm", new MemberForm());

        return "members/createMemberForm";
    }

    /**
     * 회원 등록
     */
    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult result) {

        if(result.hasErrors()){ // 에러가 있으면
            return "members/createMemberForm";
       }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members",members);

        return "members/memberList";
    }
}
