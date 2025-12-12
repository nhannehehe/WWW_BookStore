package book.ecom.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SessionCart implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer productId;
    private String productTitle;
    private String productImage;
    private Double productPrice;
    private Integer productStock;
    private Integer quantity;
    private Double totalPrice;
    
    public SessionCart(Product product, Integer quantity) {
        this.productId = product.getId();
        this.productTitle = product.getTitle();
        this.productImage = product.getImage();
        this.productPrice = product.getDiscountPrice();
        this.productStock = product.getStock();
        this.quantity = quantity;
        this.totalPrice = product.getDiscountPrice() * quantity;
    }
    
    public void incrementQuantity() {
        this.quantity++;
        this.totalPrice = this.productPrice * this.quantity;
    }
    
    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
            this.totalPrice = this.productPrice * this.quantity;
        }
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.totalPrice = this.productPrice * this.quantity;
    }
}
