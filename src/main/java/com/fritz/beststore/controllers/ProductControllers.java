package com.fritz.beststore.controllers;

import com.fritz.beststore.models.Product;
import com.fritz.beststore.models.ProductDto;
import com.fritz.beststore.services.ProductsRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Controller
@RequestMapping("/products")
public class ProductControllers {

    @Autowired
    private ProductsRepository repo;

    @GetMapping({"", "/"} )
    public String showProductList(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 10;
        Page<Product> productsPage = repo.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        // Return the correct template name here
        return "products/Createproduct";  // Make sure 'Createproduct.html' exists in the 'templates/products' folder
    }

    @PostMapping("/create")
public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
    if (productDto.getImageFile().isEmpty()) {
        result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
    }

    if (result.hasErrors()) {
        return "products/CreateProduct";
    }

    // Save image file
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

    try {
        repo.save(product); // Save to database
    } catch (Exception ex) {
        System.err.println("Error saving product: " + ex.getMessage());
        result.rejectValue("name", "error.product", "Error saving product: " + ex.getMessage());
        return "products/CreateProduct";
    }

    return "redirect:/products";
}


    @GetMapping("/details")
    public String showProductDetails(@RequestParam int id, Model model) {
        try {
            Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
            model.addAttribute("product", product);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Product not found");
            return "errorPage";
        }
        return "products/DetailsProduct";
    }


    @GetMapping("/edit")
    public String showEditPage(@RequestParam int id, Model model) {
        try {
            Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
            productDto.setId(product.getId());
            productDto.setImageFileName(product.getImageFileName());
            productDto.setCreatedAt(product.getCreatedAt());
            model.addAttribute("productDto", productDto);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Product not found");
            return "errorPage";
        }
        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(@RequestParam int id, @Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        try {
            Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

            if (!productDto.getImageFile().isEmpty()) {
                // Delete old image if new one is uploaded
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                Files.deleteIfExists(oldImagePath);

                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                // Save new image file
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImageFileName(storageFileName);
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            repo.save(product);
        } catch (Exception ex) {
            result.rejectValue("name", "error.product", "Error updating product: " + ex.getMessage());
            return "products/EditProduct";
        }
        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id, Model model) {
        try {
            Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
            String uploadDir = "public/images/";
            Path imagePath = Paths.get(uploadDir + product.getImageFileName());
            Files.deleteIfExists(imagePath);
            repo.delete(product);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error deleting product: " + ex.getMessage());
            return "errorPage";
        }
        return "redirect:/products";
    }

}
