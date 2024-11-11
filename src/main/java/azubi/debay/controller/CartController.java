package azubi.debay.controller;

import azubi.debay.AuthService;
import azubi.debay.entity.CartItem;
import azubi.debay.entity.OrderHistory;
import azubi.debay.entity.Product;
import azubi.debay.entity.User;
import azubi.debay.repository.CartItemRepository;
import azubi.debay.repository.OrderHistoryRepository;
import azubi.debay.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    private final AuthService authorizer = new AuthService();
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        User user = authorizer.checkLoggedIn(session);
        List<CartItem> cartItems = cartItemRepository.findByUser(user)
                .stream()
                .sorted(Comparator.comparing(cartItem -> cartItem.getProduct().getId()))
                .collect(Collectors.toList());
        model.addAttribute("cartItems", cartItems);
        return "cart";
    }

    @PostMapping("/cart/remove/{cartItemId}")
    public String removeCartItem(@PathVariable Long cartItemId, HttpSession session) {
        authorizer.checkLoggedIn(session);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Product product = cartItem.getProduct();

        if (cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItemRepository.save(cartItem);
        } else {
            cartItemRepository.deleteById(cartItemId);
        }

        product.setQuantity(product.getQuantity() + 1);
        productRepository.save(product);

        return "redirect:/cart";
    }

    @Transactional
    @PostMapping("/buy-all")
    public String buyAll(HttpSession session) {
        User user = authorizer.checkLoggedIn(session);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            logger.info("Cart is empty. No items to purchase.");
            return "redirect:/api/products/home";
        }

        for (CartItem cartItem : cartItems) {
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setUser(user);
            orderHistory.setProduct(cartItem.getProduct());
            orderHistory.setQuantity(cartItem.getQuantity());
            orderHistory.setPurchaseDate(LocalDateTime.now());
            orderHistoryRepository.save(orderHistory);

        }

        cartItemRepository.deleteAll(cartItems);

        logger.info("All items purchased and removed from cart.");

        return "redirect:/api/products/home";
    }
}
