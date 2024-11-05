package azubi.debay.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "products",
        uniqueConstraints = @UniqueConstraint(columnNames ="product_name")
)
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "image_url")
    private String imageUrl;
}

