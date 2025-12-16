package book.ecom.service.impl;

import book.ecom.model.Cart;
import book.ecom.model.Product;
import book.ecom.model.SessionCart;
import book.ecom.model.UserDtls;
import book.ecom.repository.CartRepository;
import book.ecom.repository.ProductRepository;
import book.ecom.repository.UserRepository;
import book.ecom.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    
    private static final String SESSION_CART_KEY = "guestCart";

    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        } else {
            cart = cartStatus;
            if (cart.getQuantity() >= product.getStock()) {
                return null;
            }
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        Cart saveCart = cartRepository.save(cart);

        return saveCart;
    }

    @Override
    public List<Cart> getCartsByUser(Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        Double totalOrderPrice = 0.0;
        List<Cart> updateCarts = new ArrayList<>();
        for (Cart c : carts) {
            Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
            c.setTotalPrice(totalPrice);
            totalOrderPrice = totalOrderPrice + totalPrice;
            c.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(c);
        }

        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer countByUserId = cartRepository.countByUserId(userId);
        return countByUserId;
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {

        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;

        if (sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;

            if (updateQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }

        } else {
            if (cart.getQuantity() >= cart.getProduct().getStock()) {
                return;
            }
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }

    }

    @Override
    public void deleteCart(Integer cid) {
        cartRepository.deleteById(cid);
    }

    @Override
    public void updateCart(Cart cart) {
        cartRepository.save(cart);
    }

    // Session cart methods for guest users
    @Override
    public void addToSessionCart(Integer productId, HttpSession session) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getStock() == 0) {
            return;
        }
        
        Map<Integer, SessionCart> cartMap = getSessionCartMap(session);
        
        SessionCart sessionCart = cartMap.get(productId);
        if (sessionCart == null) {
            sessionCart = new SessionCart(product, 1);
            cartMap.put(productId, sessionCart);
        } else {
            // Check if adding one more exceeds stock
            if (sessionCart.getQuantity() >= product.getStock()) {
                return;
            }
            sessionCart.incrementQuantity();
        }
        
        session.setAttribute(SESSION_CART_KEY, cartMap);
    }
    
    @Override
    public Map<Integer, SessionCart> getSessionCart(HttpSession session) {
        Map<Integer, SessionCart> cartMap = getSessionCartMap(session);
        
        // Validate and update cart items against current product data
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, SessionCart> entry : cartMap.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product == null || product.getStock() == 0) {
                toRemove.add(entry.getKey());
            } else {
                SessionCart cart = entry.getValue();
                if (cart.getQuantity() > product.getStock()) {
                    cart.setQuantity(product.getStock());
                }
                // Update price if changed
                cart.setProductPrice(product.getDiscountPrice());
                cart.setProductStock(product.getStock());
            }
        }
        
        // Remove invalid items
        for (Integer productId : toRemove) {
            cartMap.remove(productId);
        }
        
        if (!toRemove.isEmpty()) {
            session.setAttribute(SESSION_CART_KEY, cartMap);
        }
        
        return cartMap;
    }
    
    @Override
    public Integer getSessionCartCount(HttpSession session) {
        Map<Integer, SessionCart> cartMap = getSessionCartMap(session);
        int count = 0;
        for (SessionCart cart : cartMap.values()) {
            count += cart.getQuantity();
        }
        return count;
    }
    
    @Override
    public void updateSessionCartQuantity(Integer productId, String operation, HttpSession session) {
        Map<Integer, SessionCart> cartMap = getSessionCartMap(session);
        SessionCart sessionCart = cartMap.get(productId);
        
        if (sessionCart == null) {
            return;
        }
        
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            cartMap.remove(productId);
            session.setAttribute(SESSION_CART_KEY, cartMap);
            return;
        }
        
        if (operation.equalsIgnoreCase("de")) {
            sessionCart.decrementQuantity();
            if (sessionCart.getQuantity() <= 0) {
                cartMap.remove(productId);
            }
        } else if (operation.equalsIgnoreCase("in")) {
            if (sessionCart.getQuantity() < product.getStock()) {
                sessionCart.incrementQuantity();
            }
        }
        
        session.setAttribute(SESSION_CART_KEY, cartMap);
    }
    
    @Override
    public void removeFromSessionCart(Integer productId, HttpSession session) {
        Map<Integer, SessionCart> cartMap = getSessionCartMap(session);
        cartMap.remove(productId);
        session.setAttribute(SESSION_CART_KEY, cartMap);
    }
    
    @Override
    public void clearSessionCart(HttpSession session) {
        session.removeAttribute(SESSION_CART_KEY);
    }
    
    @Override
    public void mergeSessionCartToUserCart(Integer userId, HttpSession session) {
        Map<Integer, SessionCart> sessionCartMap = getSessionCartMap(session);
        
        if (sessionCartMap.isEmpty()) {
            return;
        }
        
        UserDtls user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }
        
        // Merge each session cart item into user's cart
        for (SessionCart sessionCart : sessionCartMap.values()) {
            Product product = productRepository.findById(sessionCart.getProductId()).orElse(null);
            if (product == null || product.getStock() == 0) {
                continue;
            }
            
            Cart existingCart = cartRepository.findByProductIdAndUserId(sessionCart.getProductId(), userId);
            
            if (existingCart == null) {
                // Create new cart entry
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setProduct(product);
                newCart.setQuantity(Math.min(sessionCart.getQuantity(), product.getStock()));
                newCart.setTotalPrice(newCart.getQuantity() * product.getDiscountPrice());
                cartRepository.save(newCart);
            } else {
                // Update existing cart entry
                int newQuantity = existingCart.getQuantity() + sessionCart.getQuantity();
                newQuantity = Math.min(newQuantity, product.getStock());
                existingCart.setQuantity(newQuantity);
                existingCart.setTotalPrice(newQuantity * product.getDiscountPrice());
                cartRepository.save(existingCart);
            }
        }
        
        // Clear session cart after merge
        clearSessionCart(session);
    }
    
    @SuppressWarnings("unchecked")
    private Map<Integer, SessionCart> getSessionCartMap(HttpSession session) {
        Object cartObj = session.getAttribute(SESSION_CART_KEY);
        if (cartObj == null) {
            return new HashMap<>();
        }
        return (Map<Integer, SessionCart>) cartObj;
    }

}
