package Gooloom_CGV_V1.web.basic;

import Gooloom_CGV_V1.domain.item.Item;
import Gooloom_CGV_V1.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/product/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "product/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));
        model.addAttribute("item", item);
        return "product/item";
    }

    @GetMapping("/addItem")
    public String addItemForm() {
        return "/product/addItemForm";
    }

    @PostMapping("/addItem")
    public String addItem(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getItemId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/product/items/{itemId}";
    }

    @GetMapping("/{itemId}/editItem")
    public String editItemForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));
        model.addAttribute("item", item);
        return "product/editItemForm";
    }

    @PostMapping("/{itemId}/editItem")
    public String editItem(@PathVariable Long itemId, @ModelAttribute Item item) {
        item.setItemId(itemId); // Assuming setId is used to update existing item
        itemRepository.save(item);
        return "redirect:/product/items/{itemId}";
    }

    @GetMapping("/{itemId}/delete")
    public String deleteItemForm(@PathVariable Long itemId, Model model) {
        Item deletedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));
        model.addAttribute("deletedItem", deletedItem);
        return "product/deleteItemForm";
    }

    @PostMapping("/{itemId}/delete")
    public String deleteItem(@PathVariable Long itemId) {
        itemRepository.deleteById(itemId);
        return "redirect:/product/items";
    }

    @GetMapping("/deleteItemAll")
    public String deleteItemAllForm() {
        return "product/deleteItemAllForm";
    }

    @PostMapping("/deleteItemAll")
    public String deleteItemAll() {
        itemRepository.deleteAll();
        return "redirect:/product/items";
    }
}
