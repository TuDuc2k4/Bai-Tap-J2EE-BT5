package com.example.bai5_qlsp.controller;

import com.example.bai5_qlsp.dto.CartItem;
import com.example.bai5_qlsp.entity.Account;
import com.example.bai5_qlsp.entity.Order;
import com.example.bai5_qlsp.entity.OrderDetail;
import com.example.bai5_qlsp.entity.OrderStatus;
import com.example.bai5_qlsp.entity.Product;
import com.example.bai5_qlsp.repository.AccountRepository;
import com.example.bai5_qlsp.repository.OrderDetailRepository;
import com.example.bai5_qlsp.repository.OrderRepository;
import com.example.bai5_qlsp.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private AccountRepository accountRepository;

    // View cart
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }
        
        long total = cart.values().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
        
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("total", total);
        return "cart";
    }

    // Add to cart
    @PostMapping("/add")
    public String addToCart(@RequestParam Integer productId, HttpSession session, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return "redirect:/home";
        }

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        if (cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            item.setQuantity(item.getQuantity() + 1);
        } else {
            cart.put(productId, new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImage(), 1));
        }

        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("successMsg", "Đã thêm " + product.getName() + " vào giỏ hàng.");
        return "redirect:/home";
    }

    // Update quantity
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Integer productId, @RequestParam String action, HttpSession session) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart != null && cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            if ("increase".equals(action)) {
                item.setQuantity(item.getQuantity() + 1);
            } else if ("decrease".equals(action)) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    cart.remove(productId);
                }
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // Delete item
    @PostMapping("/remove")
    public String removeItem(@RequestParam Integer productId, HttpSession session) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(productId);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // Checkout
    @PostMapping("/checkout")
    public String checkout(HttpSession session, Authentication authentication, RedirectAttributes redirectAttributes) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Giỏ hàng rỗng!");
            return "redirect:/cart";
        }

        String username = authentication.getName();
        Account account = accountRepository.findByLoginName(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại."));

        long total = cart.values().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setAccount(account);
        order.setTotalPrice(total);
        order.setStatus(OrderStatus.PENDING); // Status from the feature request
        order = orderRepository.save(order);

        for (CartItem item : cart.values()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(productRepository.findById(item.getProductId()).orElseThrow());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            orderDetailRepository.save(detail);
        }

        session.removeAttribute("cart");
        redirectAttributes.addFlashAttribute("successMsg", "Đặt hàng thành công! Đơn hàng của bạn đang được xử lý.");
        return "redirect:/home";
    }
}
