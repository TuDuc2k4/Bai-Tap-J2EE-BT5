package com.example.bai5_qlsp.config;

import com.example.bai5_qlsp.entity.Category;
import com.example.bai5_qlsp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter implements Converter<String, Category> {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public Category convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            Integer id = Integer.parseInt(source);
            return categoryRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
