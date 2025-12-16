package book.ecom.service;

import book.ecom.model.Cart;
import book.ecom.model.SessionCart;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

public interface CartService {

	public Cart saveCart(Integer productId, Integer userId);

	public List<Cart> getCartsByUser(Integer userId);
	
	public Integer getCountCart(Integer userId);

	public void updateQuantity(String sy, Integer cid);

	public void deleteCart(Integer cid);

	public void updateCart(Cart cart);
	
	// Session cart methods for guest users
	public void addToSessionCart(Integer productId, HttpSession session);
	
	public Map<Integer, SessionCart> getSessionCart(HttpSession session);
	
	public Integer getSessionCartCount(HttpSession session);
	
	public void updateSessionCartQuantity(Integer productId, String operation, HttpSession session);
	
	public void removeFromSessionCart(Integer productId, HttpSession session);
	
	public void clearSessionCart(HttpSession session);
	
	public void mergeSessionCartToUserCart(Integer userId, HttpSession session);
}
