package azubi.debay.repository;

import azubi.debay.entity.CartItem;
import azubi.debay.entity.Product;
import azubi.debay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUserAndProduct(User user, Product product);
    List<CartItem> findByUser(User user);
}
