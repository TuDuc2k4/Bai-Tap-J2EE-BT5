package com.example.bai5_qlsp.controller;

import com.example.bai5_qlsp.entity.Product;
import com.example.bai5_qlsp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shop")
public class UserProductController {

	@Autowired
	private ProductRepository productRepository;

	@GetMapping
	public String showProductCatalog(Model model) {
		model.addAttribute("products", productRepository.findAll());
		return "shop-product-list";
	}

	@GetMapping("/{id}")
	public String showProductDetail(@PathVariable Integer id, Model model) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
		model.addAttribute("product", product);
		return "shop-product-detail";
	}
}

