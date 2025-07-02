package com.store.elara.services;

import com.store.elara.dtos.ProductDto;
import com.store.elara.entities.*;
import com.store.elara.mappers.ProductMapper;
import com.store.elara.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final CartItemsRepository cartItemsRepository;

    private final CartRepository cartRepository;


    public List<Product> fetchAllProductsByCategory() {

        return productRepository.fetchAllProductsByCategory();
    }

    public List<Product> findByCategoryId(Byte CategoryId) {
        return productRepository.findByCategoryId(CategoryId);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }
    // ADD PRODUCT ALONG WITH IMAGE
    public ProductDto addProduct(Product product , MultipartFile multipartFile) throws IOException {
        product.setImageName(multipartFile.getOriginalFilename());
        product.setImageType(multipartFile.getContentType());
        product.setImageData(multipartFile.getBytes());
        productRepository.save(product);

        return productMapper.toProductDto(product);
    }



    // UPDATE A PRODUCT
    public Product updateProduct(Long id, ProductDto productDto, MultipartFile file) throws IOException {

        Product product1 = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        if( product1 == null){
            return null;
        }

        productMapper.updateProductDto(productDto, product1);

        Category category =  categoryRepository.findById(productDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productDto.getCategoryId()));
        if(category == null){
            return null;
        }
        product1.setCategory(category);
        product1.setImageName(file.getOriginalFilename());
        product1.setImageType(file.getContentType());
        product1.setImageData(file.getBytes());
        product1.setUpdatedAt(productDto.getCreatedAt());

        return productRepository.save(product1);
    }

    public void deleteProduct(Product product) {
        // 1. Remove from users' favorite products
        product.getUsers().forEach(user -> user.getFavoriteProducts().remove(product));
        userRepository.saveAll(product.getUsers());

        // 2. Remove from cart items
        List<CartItems> itemsWithProduct = cartItemsRepository.findAllByProduct(product);
        for (CartItems item : itemsWithProduct) {
            Cart cart = item.getCart();
            cart.getCartItems().remove(item);
            cartRepository.save(cart);
            cartItemsRepository.delete(item); // optional: batch with deleteAll()
        }

        // 3. Delete the product
        productRepository.delete(product);
    }


    // DELETE ALL PRODUCTS
    public void deleteAll() {

        productRepository.findAll().forEach((product) -> {
                    // 1. Remove from users' favorite products
                    product.getUsers().forEach(user -> user.getFavoriteProducts().remove(product));
                    userRepository.saveAll(product.getUsers());

                    // 2. Remove from cart items
                    List<CartItems> itemsWithProduct = cartItemsRepository.findAllByProduct(product);
                    for (CartItems item : itemsWithProduct) {
                        Cart cart = item.getCart();
                        cart.getCartItems().remove(item);
                        cartRepository.save(cart);
                        cartItemsRepository.delete(item); // optional: batch with deleteAll()
                    }

                    // 3. Delete the product
                    productRepository.delete(product);
        }
        );
    }

    // DELETE A WHOLE CATEGORY OF PRODUCTS
    public void deleteAllByCategoryId(Category category) {
        productRepository.fetchAllProductsByCategory().forEach((product) -> {
            // 1. Remove from users' favorite products
            product.getUsers().forEach(user -> user.getFavoriteProducts().remove(product));
            userRepository.saveAll(product.getUsers());

            // 2. Remove from cart items
            List<CartItems> itemsWithProduct = cartItemsRepository.findAllByProduct(product);
            for (CartItems item : itemsWithProduct) {
                Cart cart = item.getCart();
                cart.getCartItems().remove(item);
                cartRepository.save(cart);
                cartItemsRepository.delete(item); // optional: batch with deleteAll()
            }

            // 3. Delete the product
            productRepository.delete(product);
        });
    }
    //SEARCH A PRODUCT BY KEYWORD
    public List<ProductDto> searchByKeyword(String keyword) {
       return productRepository.searchByKeyword(keyword)
                                        .stream()
                                        .map((product)->{
                                            ProductDto productDto = productMapper.toProductDto(product);
                                            productDto.setImageUrl(product.getId()+"/images");
                                            return productDto;
                                        })
                                        .collect(Collectors.toList());
    }
}
