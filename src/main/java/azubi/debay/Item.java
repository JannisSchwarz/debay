package azubi.debay;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {

    @Column(name = "Item_Name")
    private String name;
    @Column(name = "Item_Price")
    private double price;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Item_ID")
    private int id;
}
