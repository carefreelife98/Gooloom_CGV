package Gooloom_CGV_V1.web.basic;

import Gooloom_CGV_V1.domain.kart.Kart;
import Gooloom_CGV_V1.domain.kart.KartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/kart/myKart")
@RequiredArgsConstructor
public class BasicKartController {

    private final KartRepository kartRepository;

    @GetMapping
    public String items(Model model) {
        List<Kart> karts = kartRepository.findAll();
        model.addAttribute("karts", karts);
        return "kart/karts";
    }

    @GetMapping("/{kartId}")
    public String kart(@PathVariable Long kartId, Model model) {
        Kart kart = kartRepository.findById(kartId)
                .orElseThrow(() -> new NoSuchElementException("Your Kart not found"));
        model.addAttribute("kart", kart);
        return "kart/kart";
    }

    @GetMapping("/addKart")
    public String addKartForm() {
        return "/kart/addKartForm";
    }

    @PostMapping("/addKart")
    public String addKart(Kart kart, RedirectAttributes redirectAttributes) {
        Kart savedKart = kartRepository.save(kart);
        redirectAttributes.addAttribute("kartId", savedKart.getKartId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/kart/karts/{kartId}";
    }

    @GetMapping("/{kartId}/editKart")
    public String editKartForm(@PathVariable Long kartId, Model model) {
        Kart kart = kartRepository.findById(kartId)
                .orElseThrow(() -> new NoSuchElementException("Kart not found"));
        model.addAttribute("kart", kart);
        return "kart/editKartForm";
    }

    @PostMapping("/{kartId}/editKart")
    public String editKart(@PathVariable Long kartId, @ModelAttribute Kart kart) {
        kart.setKartId(kartId); // Assuming setId is used to update existing item
        kartRepository.save(kart);
        return "redirect:/kart/karts/{kartId}";
    }

    @GetMapping("/{kartId}/delete")
    public String deleteKartForm(@PathVariable Long kartId, Model model) {
        Kart deletedKart = kartRepository.findById(kartId)
                .orElseThrow(() -> new NoSuchElementException("Kart not found"));
        model.addAttribute("deletedKart", deletedKart);
        return "kart/deleteKartForm";
    }

    @PostMapping("/{kartId}/delete")
    public String deleteKart(@PathVariable Long kartId) {
        kartRepository.deleteById(kartId);
        return "redirect:/kart/karts";
    }

    @GetMapping("/deleteKartAll")
    public String deleteKartAllForm() {
        return "kart/deleteKartAllForm";
    }

    @PostMapping("/deleteKartAll")
    public String deleteKartAll() {
        kartRepository.deleteAll();
        return "redirect:/kart/karts";
    }
}
