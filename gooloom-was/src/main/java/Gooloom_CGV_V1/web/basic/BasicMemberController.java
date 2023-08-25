package Gooloom_CGV_V1.web.basic;

import Gooloom_CGV_V1.domain.member.Member;
import Gooloom_CGV_V1.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/basic/members")
@RequiredArgsConstructor
public class BasicMemberController {

    private final MemberRepository memberRepository;

    @GetMapping
    public String members(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "basic/members";
    }

    @GetMapping("/{memberId}")
    public String member(@PathVariable Long memberId, Model model) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        model.addAttribute("member", member);
        return "basic/member";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    @PostMapping("/add")
    public String addMember(Member member, RedirectAttributes redirectAttributes) {
        Member savedMember = memberRepository.save(member);
        redirectAttributes.addAttribute("memberId", savedMember.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/members/{memberId}";
    }

    @GetMapping("/{memberId}/edit")
    public String editForm(@PathVariable Long memberId, Model model) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        model.addAttribute("member", member);
        return "basic/editForm";
    }

    @PostMapping("/{memberId}/edit")
    public String edit(@PathVariable Long memberId, @ModelAttribute Member member) {
        member.setId(memberId); // Assuming setId is used to update existing member
        memberRepository.save(member);
        return "redirect:/basic/members/{memberId}";
    }

    @GetMapping("/{memberId}/delete")
    public String deleteForm(@PathVariable Long memberId, Model model) {
        Member deletedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        model.addAttribute("deletedMember", deletedMember);
        return "basic/deleteForm";
    }

    @PostMapping("/{memberId}/delete")
    public String delete(@PathVariable Long memberId) {
        memberRepository.deleteById(memberId);
        return "redirect:/basic/members";
    }

    @GetMapping("/deleteAll")
    public String deleteAllForm() {
        return "basic/deleteAllForm";
    }

    @PostMapping("/deleteAll")
    public String deleteAll() {
        memberRepository.deleteAll();
        return "redirect:/basic/members";
    }
}
