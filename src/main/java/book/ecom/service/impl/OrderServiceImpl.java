package book.ecom.service.impl;

import book.ecom.model.*;
import book.ecom.repository.CartRepository;
import book.ecom.repository.ProductOrderRepository;
import book.ecom.repository.ProductRepository;
import book.ecom.service.OrderService;
import book.ecom.util.CommonUtil;
import book.ecom.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception {

        List<Cart> carts = cartRepository.findByUserId(userid);
        List<ProductOrder> orders = new ArrayList<ProductOrder>();
        for (Cart cart : carts) {

            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDateTime.now());

            Product product = cart.getProduct();
            order.setProduct(product);
            order.setPrice(product.getDiscountPrice());

            order.setQuantity(cart.getQuantity());

            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            // Update stock
            product.setStock(product.getStock() - cart.getQuantity());
            productRepository.save(product);

            orders.add(order);
        }
        List<ProductOrder> saveOrder = orderRepository.saveAll(orders);
        cartRepository.deleteAll(carts);
        commonUtil.sendMailForProductOrder(saveOrder, "success");
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        List<ProductOrder> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return orders;
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status);
            ProductOrder updateOrder = orderRepository.save(productOrder);
            return updateOrder;
        }
        return null;
    }

    @Override
    public ProductOrder updateOrderQuantity(Integer orderId, Integer newQuantity) {
        Optional<ProductOrder> opt = orderRepository.findById(orderId);
        if (!opt.isPresent()) {
            return null;
        }

        ProductOrder order = opt.get();

        if (OrderStatus.DELIVERED.getName().equals(order.getStatus()) || OrderStatus.SUCCESS.getName().equals(order.getStatus()) || OrderStatus.CANCEL.getName().equals(order.getStatus())) {
            return null;
        }

        if (newQuantity == null || newQuantity <= 0) {
            return null;
        }

        Product product = order.getProduct();
        int currentQty = order.getQuantity();
        int delta = newQuantity - currentQty;

        if (delta > 0) {
            if (product.getStock() < delta) {
                return null; 
            }
            product.setStock(product.getStock() - delta);
        } else if (delta < 0) {
            product.setStock(product.getStock() - delta);
        }

        order.setQuantity(newQuantity);
        productRepository.save(product);
        return orderRepository.save(order);
    }

    //BaoThong
    @Override
    public ProductOrder updateOrderStatusAdmin(Integer id, String status) {
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status);
            ProductOrder updateOrder = orderRepository.save(productOrder);
            return updateOrder;
        }
        return null;
    }
    @Override
    public boolean isProductUsedInAnyOrder(Integer productId) {
        long count = orderRepository.countByProduct_Id(productId);
        return count > 0;
    }


    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("orderDate").descending());
        return orderRepository.findAll(pageable);
    }

    @Override
    public List<Object[]> getMonthlySalesByYear(int year) {
        List<Object[]> rawData = orderRepository.getPriceByMonth(year);
        List<Object[]> result = new ArrayList<>();

        // Initialize all months with 0
        IntStream.rangeClosed(1, 12).forEach(month -> result.add(new Object[]{month, 0}));

        // Fill in the actual data
        for (Object[] data : rawData) {
            int month = (int) data[0];
            double totalPrice = (double) data[1];
            result.set(month - 1, new Object[]{month, totalPrice});
        }

        return result;
    }

    @Override
    public List<Object[]> getDailySalesByMonth(int year, int month) {
        List<Object[]> rawData = orderRepository.getPriceByDay(year, month);
        List<Object[]> result = new ArrayList<>();

        // Initialize all days with 0
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        IntStream.rangeClosed(1, daysInMonth).forEach(day -> result.add(new Object[]{day, 0}));

        // Fill in the actual data
        for (Object[] data : rawData) {
            int day = (int) data[0];
            double totalPrice = (double) data[1];
            result.set(day - 1, new Object[]{day, totalPrice});
        }

        return result;
    }

    @Override
    public List<Object[]> getTopCustomersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findTopCustomersByDateRange(startDate, endDate);
    }

    @Override
    public List<Object[]> getTopProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findTopProductsByDateRange(startDate, endDate);
    }

    public ProductOrder getOrdersByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public ProductOrder getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }
}
