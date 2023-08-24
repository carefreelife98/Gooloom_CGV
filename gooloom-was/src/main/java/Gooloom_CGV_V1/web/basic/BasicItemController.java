package Gooloom_CGV_V1.web.basic;


import Gooloom_CGV_V1.domain.item.Item;
import Gooloom_CGV_V1.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;


@Controller
@Transactional
@RequestMapping("/product/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) throws SQLException {
        List<Item> items = itemRepository.findItemAll();
        model.addAttribute("items", items);
        return "/product/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) throws SQLException {
        Item item = itemRepository.findItemById(itemId);
        model.addAttribute("item", item);
        return "product/item";
    }

    @GetMapping("/addItem")
    public String addItemForm() {
        return "/product/addItemForm";
    }

    @PostMapping("/addItem")
    public String addItem(Item item, RedirectAttributes redirectAttributes) throws SQLException {
        Item savedItem = itemRepository.itemSave(item);
        redirectAttributes.addAttribute("itemId", savedItem.getItemId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/product/items/{itemId}";
    }

    @GetMapping("/{itemId}/editItem")
    public String editItemForm(@PathVariable Long itemId, Model model) throws SQLException {
        Item item = itemRepository.findItemById(itemId);
        model.addAttribute("item", item);
        return "/product/editItemForm";
    }

    @PostMapping("/{itemId}/editItem")
    public String editItem(@PathVariable Long itemId, @ModelAttribute Item item) throws SQLException {
        itemRepository.updateItem(itemId, item.getItemName(), item.getItemPrice(), item.getItemQuantity());
        return "redirect:/product/items/{itemId}";
    }

    @GetMapping("/{itemId}/delete")
    public String deleteItemForm(@PathVariable Long itemId, Model model) throws SQLException {
        Item deletedItem = itemRepository.findItemById(itemId);
        model.addAttribute("deletedItem", deletedItem);
        return "product/deleteItemForm";
    }

    @PostMapping("/{itemId}/delete")
    public String deleteItem(@PathVariable Long itemId) throws SQLException {
        itemRepository.deleteItemById(itemId);
        return "redirect:/product/items";
    }

    @GetMapping("/deleteItemAll")
    public String deleteItemAllForm() {
        return "product/deleteItemAllForm";
    }

    @PostMapping("/deleteItemAll")
    public String deleteItemAll() throws SQLException {
        itemRepository.deleteItemAll();
        return "redirect:/product/items";
    }

}
