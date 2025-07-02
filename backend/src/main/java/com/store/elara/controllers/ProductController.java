package com.store.elara.controllers;

import com.store.elara.dtos.ProductDto;
import com.store.elara.entities.Category;
import com.store.elara.entities.Product;
import com.store.elara.mappers.ProductMapper;
import com.store.elara.mappers.UserMapper;
import com.store.elara.repositories.CategoryRepository;
import com.store.elara.repositories.ProductRepository;
import com.store.elara.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5175")
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;

    //GET ALL PRODUCTS (CATEGORY FILTERING)
    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(required = false,defaultValue = "",name = "categoryId") Byte categoryId) {
        if(categoryId == null) {

            List<ProductDto> product1 = productService.fetchAllProductsByCategory().stream()
                    .map((product) -> {
                        ProductDto productDto = productMapper.toProductDto(product);
                        productDto.setImageUrl(product.getId()+"image");
                        return productDto;
                    })
                    .toList();
           return new ResponseEntity<>(product1, HttpStatus.OK);
        }
        if(categoryRepository.existsById(categoryId)) {

            List<ProductDto> product =productService.findByCategoryId(categoryId).stream()
                    .map((product1) -> {
                        ProductDto productDto = productMapper.toProductDto(product1);
                        productDto.setImageUrl("product/"+product1.getId()+"/image");
                        return productDto;
                    })
                    .toList();
            return ResponseEntity.ok(product);
        }

        return ResponseEntity.badRequest().body("Category Not found with id:"+categoryId);



    }

    // GET A PRODUCT
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.findById(id);

            ProductDto productDto = productMapper.toProductDto(product);
            productDto.setImageUrl("/products/" + product.getId() + "/image");
            return ResponseEntity.ok(productDto);
        }
        catch(EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ADD A PRODUCT
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestPart ProductDto productDto,
    @RequestPart MultipartFile file,
    UriComponentsBuilder uriBuilder) {

        Category category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if(category == null) {
            return ResponseEntity.badRequest().build();
        }
        Product product = productMapper.toEntity(productDto);
        product.setCategory(category);
        try{
            ProductDto product1 = productService.addProduct(product,file);
            product1.setId(product.getId());
            var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();
            return ResponseEntity.created(uri).body(product1);

        }catch(Exception e){
                return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // GET A PRODUCT'S IMAGE
    @GetMapping("/{id}/image")
    public ResponseEntity<?> getImageByProductId(@PathVariable Long id) {
        Product product = null;
        byte[] imageFile = null;
        try{
             product = productService.findById(id);
             imageFile = product.getImageData();
        }catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Product Not Found with Id : "+id);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(product.getImageType()))
                .body(imageFile);
    }

    // UPDATE A PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestPart ProductDto productDto,
                                                    @RequestPart MultipartFile file) throws IOException {
        Product product = null;
        try{
            product = productService.updateProduct(id,productDto,file);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        if(product!=null){
            return ResponseEntity.ok().body("Product updated");
        }
        return ResponseEntity.badRequest().body("Failed to update product");

    }
    //DELETE ALL PRODUCTS
    @DeleteMapping
    public  ResponseEntity<String> deleteAllProducts(@RequestParam(required = false,
            defaultValue = "", name = "categoryId") Byte categoryId ) {
        if(categoryId == null) {
            productService.deleteAll();
            return ResponseEntity.noContent().build();
        }
        Category category = categoryRepository.findById(categoryId).orElse(null);
        productService.deleteAllByCategoryId(category);
        return ResponseEntity.noContent().build();
    }
    // DELETE A SINGLE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Product product = null;
        try{
             product = productService.findById(id);
             productService.deleteProduct(product);
        }catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }

    //SEARCH FOR A PRODUCT USING ITS NAME
    @GetMapping("/search")
    public ResponseEntity<?> searchByKeyword(@RequestParam(required = false,
            defaultValue = "", name = "keyword") String keyword) {
        if(keyword != null) {
            List<ProductDto> productDtos = productService.searchByKeyword(keyword);

            return ResponseEntity.ok(productDtos);
        }
        List<ProductDto> product1 = productService.fetchAllProductsByCategory().stream()
                .map((product) -> {
                    ProductDto productDto = productMapper.toProductDto(product);
                    productDto.setImageUrl(product.getId()+"image");
                    return productDto;
                })
                .toList();
        return new ResponseEntity<>(product1, HttpStatus.OK);


    }

}
