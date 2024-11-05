package azubi.debay.controller;

import azubi.debay.entity.CartItem;
import azubi.debay.entity.Product;
import azubi.debay.entity.User;
import azubi.debay.repository.CartItemRepository;
import azubi.debay.repository.ProductRepository;
import azubi.debay.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/home")
    public String getProducts(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            logger.error("User not found in session. Redirecting to login page.");
            return "redirect:/";
        }

        List<Product> sortedProducts = productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        model.addAttribute("products", sortedProducts);
        return "products";
    }

    @GetMapping("/{userId}/cart")
    public List<CartItem> getUserCart(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    @PostMapping("/add-to-cart/{productId}")
    public String addToCart(@PathVariable Long productId, HttpSession session) {
        User user = (User) session.getAttribute("user"); // Retrieve user from session
        if (user == null) {
            logger.error("User not found in the session. Redirecting to login page.");
            return "redirect:/"; // Redirect to login if user is not in session
        }

        logger.info("User '{}' is adding product with ID: {}", user.getUsername(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product is available before adding to cart
        if (product.getQuantity() <= 0) {
            logger.error("Product '{}' is out of stock.", product.getName());
            return "redirect:/api/products/home"; // Optionally show a message that the product is sold out
        }

        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);
        if (existingCartItem != null) {
            // Calculate potential new quantity in cart
            int newQuantity = existingCartItem.getQuantity() + 1;

            // Allow adding only if the new quantity doesn't exceed available stock
            if (product.getQuantity() > 0) {
                existingCartItem.setQuantity(newQuantity);
                cartItemRepository.save(existingCartItem);

                // Decrease product quantity in stock
                if(product.getQuantity() > 0) {
                    product.setQuantity(product.getQuantity() - 1);
                    productRepository.save(product);

                    logger.info("Added one more '{}' to cart. New quantity: {}", product.getName(), newQuantity);
                }
            } else {
                logger.error("Cannot add more of product '{}' to cart, would exceed available stock.", product.getName());
            }
        } else {
            // Create a new cart item if it doesn't exist
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItemRepository.save(cartItem);

            // Decrease product quantity after adding to the cart
            product.setQuantity(product.getQuantity() - 1);
            productRepository.save(product);

            logger.info("Product '{}' added to cart successfully. Quantity in cart: {}", product.getName(), 1);
        }

        logger.info(getUserCart(user.getId()).toString());

        return "redirect:/api/products/home";
    }
}



