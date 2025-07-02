package com.store.elara.controllers;

import com.store.elara.dtos.*;
import com.store.elara.entities.*;
import com.store.elara.mappers.CartItemsMapper;
import com.store.elara.mappers.CartMapper;
import com.store.elara.mappers.ProductMapper;
import com.store.elara.mappers.UserMapper;
import com.store.elara.repositories.*;
import com.store.elara.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:5175")
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepositories;

    private final ProductRepository productRepositories;

    private final UserMapper userMapper;

    private final ProductMapper productMapper;

    private final CartMapper cartMapper;

    private final CartRepository cartRepositories;

    private final CartItemsMapper cartItemsMapper;

    private final UserService userService;

        // Made a comment
    //Login User
    @PostMapping("/login")
    public String UserLogin(@RequestBody RegisterUserRequest request) {
        return userService.login(request);
    }


    // GET ALL USERS #1
    @GetMapping
    public Iterable<UserDto> getAllUsers(@RequestParam(required = false,defaultValue = "",name="sort") String sort) {

        if(!Set.of("name","email").contains(sort))
            sort = "name";

        return userRepositories.findAll(Sort.by(sort))
                .stream()
                .map(userMapper::toDto)
                .toList();

    }

    // GET A USER BY ID #2
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
         var user = userRepositories.findById(id).orElse(null);
        if(user == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    // POST A USER #3
    @PostMapping("/register")
    public ResponseEntity<UserDto> addUser(@RequestBody RegisterUserRequest request,
    UriComponentsBuilder uriBuilder) {

        UserDto userDto = userService.add(request);

        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    //UPDATE A USER #4
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserRequest updateUserRequest, @PathVariable(name="id") Long id) {

            UserDto userDto = userService.update(updateUserRequest,id);

            if(userDto == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(userDto);
    }

    //DELETE A USER #5
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        var user = userRepositories.findById(id).orElse(null);
        if(user == null)
            return ResponseEntity.notFound().build();
        userRepositories.delete(user);
        return ResponseEntity.noContent().build();

    }

    //CHANGE A USER PASSWORD
    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        String change =  userService.changePassword(request,id);

        if(change == null)
            return ResponseEntity.notFound().build();
        if(change.equals("Wrong password")){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    // ADD A PRODUCT TO A USER'S WISHLIST
    @PostMapping("/{userId}/wishlist/{productId}/product")
    public ResponseEntity<Void> registerUser(@PathVariable Long userId, @PathVariable Long productId) {
            User user = userRepositories.findById(userId).orElse(null);
            Product product = productRepositories.findById(productId).orElse(null);
            if(user == null || product == null){
                return ResponseEntity.notFound().build();
            }
            user.getFavoriteProducts().add(product);
            userRepositories.save(user);
            return ResponseEntity.ok().build();
    }

    //GET A USER'S WISHLIST
    @GetMapping("/{id}/wishlist")
    public ResponseEntity<Set<ProductDto>> getWishlist(@PathVariable Long id){
        User user = userRepositories.findById(id).orElse(null);
        if(user == null)
            return ResponseEntity.notFound().build();
        Set<ProductDto> products = user.getFavoriteProducts().stream()
                .map(productMapper::toProductDto)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(products);

    }

    // DELETE A PRODUCT FROM A USER'S WISHLIST
    @DeleteMapping("/{userId}/wishlist/{productId}/product")
    public ResponseEntity<Void> deleteProductFromWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        User user = userRepositories.findById(userId).orElse(null);
        Product product = productRepositories.findById(productId).orElse(null);
        if(user == null || product == null){
            return ResponseEntity.notFound().build();
        }
        Set<Product> products = user.getFavoriteProducts();
        if(!products.contains(product)){
            return ResponseEntity.notFound().build();
        }
        user.getFavoriteProducts().remove(product);
        userRepositories.save(user);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/carts")
    public ResponseEntity<CartDto> getCarts(@PathVariable Long id){
        User user = userRepositories.findById(id).orElse(null);
        if(user == null)
            return ResponseEntity.notFound().build();
        Cart cart = user.getCart();

        // converting each cartItem in user's cart's List<cartItem> to cartItemsDto
        List<CartItemsDto> cartItems = cart.getCartItems().stream()
                        .map((cartItem)->{
                            ProductDto productDto = productMapper.toProductDto(cartItem.getProduct());
                            CartItemsDto ctd =  cartItemsMapper.toCartItemsDto(cartItem);
                            ctd.setProductDto(productDto);
                            return ctd;
                            })
                .toList();

        CartDto cartDto = cartMapper.toCartDto(cart);
        cartDto.setCartItemsDto(cartItems);

        return ResponseEntity.ok(cartDto);
    }

    /*// ADDING A ITEM TO A USER'S CART (


    //  3 --> Find the user's cart using the user found
    //  4 --> add the cartItem obj to the list of cartItems in cart
    //  5 --> then save the cart to the cart repo
    //  6 --> the save the user to user repo

    // )*/
    @PostMapping("/{id}/carts/add")
    public ResponseEntity<Void> addItemToCart(@PathVariable Long id, @RequestBody CartItemsDto item,
                                              UriComponentsBuilder uriBuilder) {
        // 1. Find user
        User user = userRepositories.findById(id).orElse(null);
        if(user == null)
            return ResponseEntity.notFound().build();

        // 2. Get user's cart
        Cart cart = user.getCart();

        // 3. Convert DTOs to entities
        Product product = productMapper.toEntity(item.getProductDto());
        CartItems cartItems = cartItemsMapper.toCartItems(item);

        // 4. Set relationships
        cartItems.setProduct(product);
        cartItems.setCart(cart);

        // 5. Add to cart's collection

        cart.getCartItems().add(cartItems);

        // 6. Save only the cart - cascade will handle cartItems
        cartRepositories.save(cart);

        var uri = uriBuilder.path("/users/{id}/cart/add").buildAndExpand(cart.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

}

