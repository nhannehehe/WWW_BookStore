package book.ecom.service;

import book.ecom.model.OrderRequest;
import book.ecom.model.ProductOrder;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception;

    public List<ProductOrder> getOrdersByUser(Integer userId);

    public ProductOrder updateOrderStatus(Integer id, String status);

    public ProductOrder updateOrderQuantity(Integer orderId, Integer newQuantity);

    //BaoThong
    public ProductOrder updateOrderStatusAdmin(Integer id, String status);

    public boolean isProductUsedInAnyOrder(Integer productId);

    public List<ProductOrder> getAllOrders();

    public ProductOrder getOrdersByOrderId(String orderId);

    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);

    public ProductOrder getOrderById(Integer id);

    public List<Object[]> getMonthlySalesByYear(int year);

    //Thống kê theo ngày
    public List<Object[]> getDailySalesByMonth(int year, int month);

    //TK KH
    List<Object[]> getTopCustomersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    //TK SP
    List<Object[]> getTopProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
