package com.ptit.edu.store.product.models.data;

import com.ptit.edu.store.product.models.body.DetailBody;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "OrderDetail")
public class OrderDetail {
    public static final String ID = "id";

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name= "OrderId")
    private Order order;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name= "ProductId")
    private Product product;

    private int quantity;
    private double price;
    private double total;
    private String size;

    public OrderDetail() {
    }

    public void addDetail(DetailBody body) {
        this.quantity = body.getQuantity();
        this.price = product.getPrice();
        this.total = (double) quantity * price;
        this.size = body.getSize();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
