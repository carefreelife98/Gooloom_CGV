package Gooloom_CGV_V1.web.basic;

import Gooloom_CGV_V1.domain.member.Member;
import Gooloom_CGV_V1.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/basic/members")
@RequiredArgsConstructor
@Transactional
public class BasicMemberController {

    private final MemberRepository memberRepository;

    @GetMapping
    public String members(Model model) throws SQLException {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "basic/members";
    }

    @GetMapping("/{memberId}")
    public String member(@PathVariable long memberId, Model model) throws SQLException {
        Member member = memberRepository.findById(memberId);
        model.addAttribute("member", member);
        return "basic/member";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    @PostMapping("/add")
    public String addMember(Member member, RedirectAttributes redirectAttributes) throws SQLException {
        Member savedMember = memberRepository.save(member);
        redirectAttributes.addAttribute("memberId", savedMember.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/members/{memberId}";
    }

    @GetMapping("/{memberId}/edit")
    public String editForm(@PathVariable Long memberId, Model model) throws SQLException {
        Member member = memberRepository.findById(memberId);
        model.addAttribute("member", member);
        return "basic/editForm";
    }

    @PostMapping("/{memberId}/edit")
    public String edit(@PathVariable Long memberId, @ModelAttribute Member member) throws SQLException {
        memberRepository.update(member.getId(), member.getMemberName(), member.getTel());
        return "redirect:/basic/members/{memberId}";
    }

    @GetMapping("/{memberId}/delete")
    public String deleteForm(@PathVariable Long memberId, Model model) throws SQLException {
        Member deletedMember = memberRepository.findById(memberId);
        model.addAttribute("deletedMember", deletedMember);
        return "basic/deleteForm";
    }

    @PostMapping("/{memberId}/delete")
    public String delete(@PathVariable Long memberId) throws SQLException {
        memberRepository.deleteById(memberId);
        return "redirect:/basic/members";
    }

    @GetMapping("/deleteAll")
    public String deleteAllForm() throws SQLException {
        return "basic/deleteAllForm";
    }

    @PostMapping("/deleteAll")
    public String deleteAll() throws SQLException {
        memberRepository.clearGroup();
        return "redirect:/basic/members";
    }
}
