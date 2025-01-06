package com.fritz.beststore.controllers;

import com.fritz.beststore.models.Product;
import com.fritz.beststore.services.ProductsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductControllers {

    @Autowired
    private ProductsRepository repo;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        // Fetch all products from the repository
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        // Add the list of products to the model
        model.addAttribute("products", products);
        // Return the view name to be rendered (index.html inside the 'products' folder)
        return "products/index";
    }
}
