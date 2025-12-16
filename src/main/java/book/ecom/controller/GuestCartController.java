package book.ecom.controller;

import book.ecom.model.Category;
import book.ecom.model.SessionCart;
import book.ecom.service.CartService;
import book.ecom.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/guest")
public class GuestCartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, HttpSession session) {
        cartService.addToSessionCart(pid, session);
        session.setAttribute("succMsg", "Đã thêm sản phẩm vào giỏ hàng");
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String viewCart(Model m, HttpSession session) {
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", allActiveCategory);

        Map<Integer, SessionCart> sessionCartMap = cartService.getSessionCart(session);
        List<SessionCart> carts = new ArrayList<>(sessionCartMap.values());

        m.addAttribute("carts", carts);
        m.addAttribute("isGuestCart", true);

        // Add cart count
        Integer countCart = cartService.getSessionCartCount(session);
        m.addAttribute("countCart", countCart);

        if (!carts.isEmpty()) {
            Double totalOrderPrice = 0.0;
            for (SessionCart cart : carts) {
                totalOrderPrice += cart.getTotalPrice();
            }
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }

        return "/guest/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer pid, HttpSession session) {
        cartService.updateSessionCartQuantity(pid, sy, session);
        return "redirect:/guest/cart";
    }

    @GetMapping("/cartDeleteProduct")
    public String deleteProduct(@RequestParam Integer pid, HttpSession session) {
        cartService.removeFromSessionCart(pid, session);
        return "redirect:/guest/cart";
    }
}
