package com.example.bai5_qlsp.controller;

import com.example.bai5_qlsp.entity.Category;
import com.example.bai5_qlsp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "category-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "category-form";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category) {
        categoryRepository.save(category);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "category-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        // Lưu ý: Sẽ có lỗi khóa ngoại nếu bạn xóa một danh mục đang chứa sản phẩm bên trong.
        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }
}