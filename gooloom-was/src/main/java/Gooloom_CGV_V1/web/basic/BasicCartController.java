package Gooloom_CGV_V1.web.basic;

import Gooloom_CGV_V1.domain.cart.Cart;
import Gooloom_CGV_V1.domain.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/cart/myCart")
@RequiredArgsConstructor
public class BasicCartController {

    private final CartRepository cartRepository;

    @GetMapping
    public String items(Model model) {
        List<Cart> carts = cartRepository.findAll();
        model.addAttribute("carts", carts);
        return "cart/carts";
    }

//    @GetMapping("/{kartId}")
//    public String kart(@PathVariable Long kartId, Model model) {
//        Cart kart = kartRepository.findById(kartId)
//                .orElseThrow(() -> new NoSuchElementException("Your Cart not found"));
//        model.addAttribute("kart", kart);
//        return "kart/kart";
//    }

//    @GetMapping("/addCart")
//    public String addCartForm() {
//        return "/cart/addCartForm";
//    }

    @PostMapping("/addCart")
    public String addCart(@RequestParam("cartPrice") int cartPrice, @RequestParam("itemId") Long itemId,
                          @RequestParam("cartImage") String cartImage, @RequestParam("cartName") String name,
                          Cart cart, RedirectAttributes redirectAttributes) {
        cart.setCartPrice(cartPrice);
        cart.setItemId(itemId);
        cart.setImage(cartImage);
        cart.setItemName(name);
        System.out.println("cart = " + cart);
        Cart savedCart = cartRepository.save(cart);
        redirectAttributes.addAttribute("cartId", savedCart.getCartId());
        redirectAttributes.addAttribute("status", true);
        return "/cart/saveCart";
    }

    @GetMapping("/{cartId}/editCart")
    public String editCartForm(@PathVariable Long cartId, Model model) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));
        model.addAttribute("cart", cart);
        return "cart/editCartForm";
    }

    @PostMapping("/{cartId}/editCart")
    public String editCart(@PathVariable Long cartId, @ModelAttribute Cart cart) {
        cart.setCartId(cartId); // Assuming setId is used to update existing item
        cartRepository.save(cart);
        return "redirect:/cart/carts/{cartId}";
    }

    @GetMapping("/{cartId}/delete")
    public String deleteCartForm(@PathVariable Long cartId, Model model) {
        Cart deletedCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));
        model.addAttribute("deletedCart", deletedCart);
        return "cart/deleteCartForm";
    }

    @PostMapping("/{cartId}/delete")
    public String deleteCart(@PathVariable Long cartId) {
        cartRepository.deleteById(cartId);
        return "redirect:/cart/carts";
    }

    @GetMapping("/deleteKartAll")
    public String deleteCartAllForm() {
        return "cart/deleteCartAllForm";
    }

    @PostMapping("/deleteKartAll")
    public String deleteCartAll() {
        cartRepository.deleteAll();
        return "redirect:/cart/myCart";
    }

    // 구매 db 테이블 필요?
    @GetMapping("/payment")
    public String payment() {
        return "/cart/payment";
    }
}
