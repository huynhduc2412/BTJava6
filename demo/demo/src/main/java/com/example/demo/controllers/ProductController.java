package com.example.demo.controllers;

import com.example.demo.models.Product;
import com.example.demo.models.ProductImages;
import com.example.demo.services.CategoryService;
import com.example.demo.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    private static String UPLOAD_DIR = "src/main/resources/static/images/";

    // Display a list of all products
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/product-list";
    }
    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); // Load categories
        return "products/add-product";
    }
    private String convertToBase64(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result, Model model, @RequestParam("fileImage") MultipartFile imageFile, @RequestParam("multiFileImage") MultipartFile[] files) {
        if (!imageFile.isEmpty()) {
            try {
                byte[] bytes = imageFile.getBytes();
                Path path = Paths.get(UPLOAD_DIR + imageFile.getOriginalFilename());
                Files.write(path, bytes);
                product.setImage(imageFile.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<ProductImages> productImages = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    // Save the file
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                    Files.write(path, bytes);

                    // Create ProductImages object
                    ProductImages productImage = new ProductImages();
                    productImage.setImageUrl(file.getOriginalFilename());
                    productImage.setProduct(product);

                    // Add to the list
                    productImages.add(productImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        product.setImages(productImages);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
        }

        productService.addProduct(product);
        return "redirect:/products";
    }
    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories()); // Load categories
        return "products/update-product";
    }
    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result, @RequestParam("image") MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                product.setImage(file.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (result.hasErrors()) {
            product.setId(id); // set id to keep it in the form in case of errors
            model.addAttribute("categories", categoryService.getAllCategories()); // Load categories
            return "products/update-product";
        }
        productService.updateProduct(product);
        return "redirect:/products";
    }
    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    public String listProducts(@RequestParam(name = "query", required = false) String query, Model model) {
        List<Product> products;
        if (query != null && !query.isEmpty()) {
            products = productService.searchProducts(query);
            model.addAttribute("query", query);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "products/search";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        return listProducts(query, model);
    }
}
