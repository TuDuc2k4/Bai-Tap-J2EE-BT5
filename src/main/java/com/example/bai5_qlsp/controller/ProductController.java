package com.example.bai5_qlsp.controller;

import com.example.bai5_qlsp.entity.Category;
import com.example.bai5_qlsp.entity.Product;
import com.example.bai5_qlsp.repository.CategoryRepository;
import com.example.bai5_qlsp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "product-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setCategory(new Category()); // Initialize empty category to avoid null
        model.addAttribute("product", product);
        // Lấy danh sách category để đổ vào thẻ <select>
        model.addAttribute("categories", categoryRepository.findAll());
        return "product-form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product) {
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "product-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        productRepository.deleteById(id);
        return "redirect:/products";
    }
}