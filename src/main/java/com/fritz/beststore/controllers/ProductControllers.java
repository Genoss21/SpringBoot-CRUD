package com.fritz.beststore.controllers;

import com.fritz.beststore.models.Product;
import com.fritz.beststore.models.ProductDto;
import com.fritz.beststore.services.ProductsRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "products/index";  // Ensure you have 'src/main/resources/templates/products/index.html'
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        // Return the correct template name here
        return "products/Createproduct";  // Make sure 'Createproduct.html' exists in the 'templates/products' folder
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto,
        BindingResult result) {
        //TODO: process POST request

        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }

        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        //save image file
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            // Define the upload directory
            String uploadDir = "public/images";
            Path uploadPath = Paths.get(uploadDir);
        
            // Ensure the directory exists; create it if not
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        
            // Save the uploaded image to the folder
            try (InputStream inputStream = image.getInputStream()) {
                Path filePath = uploadPath.resolve(storageFileName); // Append the file name to the directory
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            // Handle exceptions and log the error
            System.err.println("Error saving file: " + ex.getMessage());
        }
        
        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);   
        
        repo.save(product);
        
        return "redirect:/products";
    }
    @GetMapping("/edit")
public String showEditPage(Model model, @RequestParam int id) {
    try {
        // Fetch the product by id
        Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        // Create and populate the ProductDto with product data
        ProductDto productDto = new ProductDto();
        productDto.setName(product.getName());
        productDto.setBrand(product.getBrand());
        productDto.setCategory(product.getCategory());
        productDto.setPrice(product.getPrice());
        productDto.setDescription(product.getDescription());
        productDto.setId(product.getId());
        productDto.setImageFileName(product.getImageFileName());

        // Ensure the createdAt field is set
        productDto.setCreatedAt(product.getCreatedAt()); // Assuming createdAt is a field in Product

        // Add ProductDto to the model
        model.addAttribute("productDto", productDto);
    } catch (Exception ex) {
        System.out.println("Exception: " + ex.getMessage());
        // Optionally add a message to the model or handle the error gracefully
        model.addAttribute("errorMessage", "Product not found");
        return "errorPage"; // Redirect to an error page if needed
    }

    return "products/EditProduct"; // Return to the EditProduct page
}

    

}