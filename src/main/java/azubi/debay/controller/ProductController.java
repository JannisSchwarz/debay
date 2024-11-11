package azubi.debay.controller;

import azubi.debay.AuthService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    private final AuthService authorizer = new AuthService();
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/home")
    public String getProducts(Model model, HttpSession session) {
        authorizer.checkLoggedIn(session);
        model.addAttribute("products", getSortedProducts());
        return "products";
    }

    @GetMapping("/{userId}/cart")
    public List<CartItem> getUserCart(@PathVariable Long userId, HttpSession session) {
        authorizer.checkLoggedIn(session);
        User user = findUserById(userId);
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    @PostMapping("/add-to-cart/{productId}")
    public String addToCart(@PathVariable Long productId, HttpSession session) {
        User user = authorizer.checkLoggedIn(session);
        Product product = findProductById(productId);

        if (!isProductInStock(product)) {
            logOutOfStockError(product);
            return "redirect:/api/products/home";
        }

        addOrUpdateCartItem(user, product);
        return "redirect:/api/products/home";
    }

    // Utility Methods

    private List<Product> getSortedProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private boolean isProductInStock(Product product) {
        return product.getQuantity() > 0;
    }

    private void logOutOfStockError(Product product) {
        logger.error("Product '{}' is out of stock.", product.getName());
    }

    private void addOrUpdateCartItem(User user, Product product) {
        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product);

        if (cartItem != null) {
            updateExistingCartItem(cartItem, product);
        } else {
            createNewCartItem(user, product);
        }
    }

    private void updateExistingCartItem(CartItem cartItem, Product product) {
        int newQuantity = cartItem.getQuantity() + 1;
        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        updateProductStock(product);
        logger.info("Added one more '{}' to cart. New quantity: {}", product.getName(), newQuantity);
    }

    private void createNewCartItem(User user, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItemRepository.save(cartItem);

        updateProductStock(product);
        logger.info("Product '{}' added to cart successfully. Quantity in cart: {}", product.getName(), 1);
    }

    private void updateProductStock(Product product) {
        if (isProductInStock(product)) {
            product.setQuantity(product.getQuantity() - 1);
            productRepository.save(product);
        } else {
            logger.error("Cannot add more of product '{}' to cart, would exceed available stock.", product.getName());
        }
    }
}
