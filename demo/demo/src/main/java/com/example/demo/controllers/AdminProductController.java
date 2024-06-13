package com.example.demo.controllers;

import com.example.demo.models.Product;
import com.example.demo.models.ProductImages;
import com.example.demo.services.CategoryService;
import com.example.demo.services.ProductService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RequestMapping("admin/products")
@Controller
public class AdminProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("listproduct", productService.getAllProducts());
        return "admin/products/index.html";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("listCategory", categoryService.getAllCategories());
        return "admin/products/create";
    }

    @PostMapping("/create")
    public String create(@Valid Product product, BindingResult result, @RequestParam("imageList") MultipartFile[] images) {
        if (result.hasErrors()) {
            return "products/add-product";
        }

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                try {
                    String imageUrl = saveImageStatic(image);
                    ProductImages productImage = new ProductImages();
                    productImage.setPhoto("/images/" + imageUrl);
                    productImage.setProduct(product); // Set the product reference
                    product.getProductImages().add(productImage); // Add the image to the product
                } catch (IOException e) {
                    e.printStackTrace();
                    // Optionally add error handling for individual image failures
                }
            }
        }

        productService.addProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/products/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid Product product, BindingResult result) {
        if (result.hasErrors()) {
            product.setId(id);
            return "admin/products/edit";
        }
//        if (imageProduct != null && imageProduct.getSize() > 0) {
//            try {
//                File saveFile = new ClassPathResource("static/images").getFile();
//                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + editProduct.getImage());
//                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        productService.updateProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/products";
    }
    private String saveImageStatic(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/images").getFile();
        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }
}