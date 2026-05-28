package book.ecom.controller;

import book.ecom.model.*;
import book.ecom.service.*;
import book.ecom.util.CommonUtil;
import book.ecom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.upload.directory:static/img/category_img}")
    private String uploadDirectory;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", allActiveCategory);
    }

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m) {
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        // m.addAttribute("categorys", categoryService.getAllCategory());
        Page<Category> page = categoryService.getAllCategorPagination(pageNo, pageSize);
        List<Category> categorys = page.getContent();
        m.addAttribute("categorys", categorys);

        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) {
        try {
            // Check for duplicate category name
            List<Category> existingCategories = categoryService.getAllCategory();
            for (Category existingCategory : existingCategories) {
                if (existingCategory.getName().equalsIgnoreCase(category.getName())) {
                    session.setAttribute("errorMsg", "Category name already exists");
                    return "redirect:/admin/category";
                }
            }

            if (file != null && !file.isEmpty()) {
                // Create unique file name
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                category.setImageName(fileName);

                // Save file
                saveImageFile(file, fileName);
            } else {
                category.setImageName("default-category.jpg");
            }

            categoryService.saveCategory(category);
            session.setAttribute("succMsg", "Category saved successfully");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Error saving category: " + e.getMessage());
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        
        // Kiểm tra category có products không
        if (categoryService.isCategoryUsedInAnyProduct(id)) {
            session.setAttribute("errorMsg", 
                "Không thể xóa loại sản phẩm vì vẫn còn sản phẩm thuộc loại này.");
            return "redirect:/admin/category";
        }

        Boolean deleteCategory = categoryService.deleteCategory(id);

        if (deleteCategory) {
            session.setAttribute("succMsg", "category delete success");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model m) {
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,
                                 @RequestParam("file") MultipartFile file,
                                 HttpSession session) throws IOException {
        try {
            Category oldCategory = categoryService.getCategoryById(category.getId());
            if (oldCategory == null) {
                session.setAttribute("errorMsg", "Category not found");
                return "redirect:/admin/category";
            }

            // Update basic info
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());

            // Handle image
            if (!file.isEmpty()) {
                // Validate image
                if (!file.getContentType().startsWith("image/")) {
                    session.setAttribute("errorMsg", "Only image files are allowed");
                    return "redirect:/admin/category";
                }

                // Generate unique filename
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                oldCategory.setImageName(fileName);

                // Delete old image if it exists and is not a URL or default image
                if (!oldCategory.isImageUrl() && !"default-category.jpg".equals(oldCategory.getImageName())) {
                    deleteOldImage(oldCategory.getImageName());
                }

                // Save new image
                saveImageFile(file, fileName);
            }

            Category updatedCategory = categoryService.saveCategory(oldCategory);
            if (updatedCategory != null) {
                session.setAttribute("succMsg", "Category updated successfully");
            } else {
                session.setAttribute("errorMsg", "Failed to update category");
            }

        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/loadEditCategory/" + category.getId();
    }

    private void saveImageFile(MultipartFile file, String fileName) throws IOException {
        // Tạo đường dẫn tuyệt đối đến thư mục upload
        String uploadPath = new File("static/img/category_img").getAbsolutePath();
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Lưu file
        Path destination = Paths.get(uploadPath, fileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    }

    // 3. Update the deleteOldImage method
    private void deleteOldImage(String fileName) {
        try {
            File staticDir = new ClassPathResource("static").getFile();
            File oldFile = new File(staticDir, uploadDirectory + "/" + fileName);
            if (oldFile.exists() && !fileName.equals("default-category.jpg")) {
                oldFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                              @RequestParam("categoryId") int categoryId,
                              HttpSession session) throws IOException {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        Category category = categoryService.getCategoryById(categoryId);
        product.setCategory(category);
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(saveProduct)) {

            File saveFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                    + image.getOriginalFilename());

            // System.out.println(path);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            session.setAttribute("succMsg", "Product Saved Success");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
                                  @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

//		List<Product> products = null;
//		if (ch != null && ch.length() > 0) {
//			products = productService.searchProduct(ch);
//		} else {
//			products = productService.getAllProducts();
//		}
//		m.addAttribute("products", products);

        Page<Product> page = null;
        if (ch != null && ch.length() > 0) {
            page = productService.searchProductPagination(pageNo, pageSize, ch);
        } else {
            page = productService.getAllProductsPagination(pageNo, pageSize);
        }
        m.addAttribute("products", page.getContent());

        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {

        // 1. Kiểm tra sản phẩm đã xuất hiện trong đơn hàng nào chưa
        if (orderService.isProductUsedInAnyOrder(id)) {
            session.setAttribute("errorMsg",
                    "Không thể xóa sản phẩm vì đã có đơn hàng chứa sản phẩm này.");
            return "redirect:/admin/products";
        }

        // 2. Nếu không có đơn hàng nào chứa product này thì cho phép xóa
        Boolean deleteProduct = productService.deleteProduct(id);
        if (deleteProduct) {
            session.setAttribute("succMsg", "Product delete success");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model m) {
        m.addAttribute("product", productService.getProductById(id));
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                                HttpSession session, Model m, @RequestParam("categoryId") int categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        product.setCategory(category);
        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("errorMsg", "invalid Discount");
        } else {
            Product updateProduct = productService.updateProduct(product, image);
            if (!ObjectUtils.isEmpty(updateProduct)) {
                session.setAttribute("succMsg", "Product update success");
            } else {
                session.setAttribute("errorMsg", "Something wrong on server");
            }
        }
        return "redirect:/admin/editProduct/" + product.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model m, @RequestParam Integer type) {
        List<UserDtls> users = null;
        if (type == 1) {
            users = userService.getUsers("ROLE_USER");
        } else {
            users = userService.getUsers("ROLE_ADMIN");
        }

        Map<Integer, Boolean> hasOrdersMap = new HashMap<>();
        for (UserDtls user : users) {
            List<ProductOrder> orders = orderService.getOrdersByUser(user.getId());
            hasOrdersMap.put(user.getId(), orders != null && !orders.isEmpty());
        }

        m.addAttribute("userType", type);
        m.addAttribute("users", users);
        m.addAttribute("hasOrdersMap", hasOrdersMap);
        return "/admin/users";
    }


    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("file") MultipartFile file, HttpSession session)
            throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);

        UserDtls saveUser = null;
        if ("ROLE_ADMIN".equals(user.getRole())) {
            saveUser = userService.saveAdmin(user);
        } else {
            saveUser = userService.saveUser(user);
        }

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Register successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        Integer t = "ROLE_ADMIN".equals(user.getRole()) ? 2 : 1;
        return "redirect:/admin/addUser?type=" + t;
    }

    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable int id, Model m) {
        UserDtls u = userService.getUserById(id);
        m.addAttribute("user", u);
        Integer type = 1;
        if (u != null && "ROLE_ADMIN".equals(u.getRole())) {
            type = 2;
        }
        m.addAttribute("userType", type);
        return "admin/edit_user";
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute UserDtls user, @RequestParam("file") MultipartFile file,
                             @RequestParam(name = "newPassword", required = false) String newPassword,
                             HttpSession session)
            throws IOException {

        UserDtls old = userService.getUserById(user.getId());

        if (old == null) {
            session.setAttribute("errorMsg", "User not found");
            return "redirect:/admin/users?type=1";
        }

        // update fields (do not change password here)
        old.setName(user.getName());
        old.setEmail(user.getEmail());
        old.setMobileNumber(user.getMobileNumber());
        old.setAddress(user.getAddress());
        old.setCity(user.getCity());
        old.setState(user.getState());
        old.setPincode(user.getPincode());
        old.setRole(user.getRole());
        old.setIsEnable(user.getIsEnable());

        if (!file.isEmpty()) {
            old.setProfileImage(file.getOriginalFilename());
        }

        UserDtls updateUser = userService.updateUser(old);

        // If admin provided a new password, update it but don't expose old password
        if (newPassword != null && newPassword.trim().length() > 0) {
            boolean pwdOk = userService.updatePassword(old, newPassword);
            if (pwdOk) {
                // reload updated user so we have latest state
                updateUser = userService.getUserById(old.getId());
            }
        }
        if (!ObjectUtils.isEmpty(updateUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "User updated successfully");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }

        return "redirect:/admin/editUser/" + user.getId() + "?type=" + 1;
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session) {

        UserDtls u = userService.getUserById(id);
        if (ObjectUtils.isEmpty(u)) {
            session.setAttribute("errorMsg", "User not found");
            return "redirect:/admin/users?type=1";
        }

        // Prevent deleting if user has orders
        List<ProductOrder> orders = orderService.getOrdersByUser(id);
        if (orders != null && !orders.isEmpty()) {
            session.setAttribute("errorMsg", "Không thể xóa user vì đã có đơn hàng liên quan.");
            Integer t = "ROLE_ADMIN".equals(u.getRole()) ? 2 : 1;
            return "redirect:/admin/users?type=" + t;
        }

        Boolean deleted = userService.deleteUser(id);
        if (deleted) {
            session.setAttribute("succMsg", "User delete success");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }

        Integer t = "ROLE_ADMIN".equals(u.getRole()) ? 2 : 1;
        return "redirect:/admin/users?type=" + t;
    }

    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, @RequestParam Integer type, HttpSession session) {
        Boolean f = userService.updateAccountStatus(id, status);
        if (f) {
            session.setAttribute("succMsg", "Account Status Updated");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/users?type=" + type;
    }

    @GetMapping("/orders")
    public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
//		List<ProductOrder> allOrders = orderService.getAllOrders();
//		m.addAttribute("orders", allOrders);
//		m.addAttribute("srch", false);

        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
        m.addAttribute("orders", page.getContent());
        m.addAttribute("srch", false);

        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "/admin/orders";
    }

    @GetMapping("/order/{id}")
    public String viewOrderDetail(@PathVariable Integer id, Model m, HttpSession session) {
        ProductOrder order = orderService.getOrderById(id);
        if (order == null) {
            session.setAttribute("errorMsg", "Order not found");
            return "redirect:/admin/orders";
        }
        m.addAttribute("orderDtls", order);
        return "/admin/order_detail";
    }

    @PostMapping("/update-order-quantity")
    public String updateOrderQuantity(@RequestParam Integer id, @RequestParam Integer newQty, HttpSession session) {
        ProductOrder updated = orderService.updateOrderQuantity(id, newQty);
        if (updated == null) {
            session.setAttribute("errorMsg", "Failed to update quantity (maybe not enough stock or invalid state)");
        } else {
            session.setAttribute("succMsg", "Order quantity updated");
        }

        return "redirect:/admin/order/" + id;
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

        try {
            commonUtil.sendMailForUpdate(updateOrder, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "status not updated");
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/search-order")
    public String searchProduct(@RequestParam String orderId, Model m, HttpSession session,
                                @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        if (orderId != null && orderId.length() > 0) {

            ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());

            if (ObjectUtils.isEmpty(order)) {
                session.setAttribute("errorMsg", "Incorrect orderId");
                m.addAttribute("orderDtls", null);
            } else {
                m.addAttribute("orderDtls", order);
            }

            m.addAttribute("srch", true);
        } else {
//			List<ProductOrder> allOrders = orderService.getAllOrders();
//			m.addAttribute("orders", allOrders);
//			m.addAttribute("srch", false);

            Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
            m.addAttribute("orders", page);
            m.addAttribute("srch", false);

            m.addAttribute("pageNo", page.getNumber());
            m.addAttribute("pageSize", pageSize);
            m.addAttribute("totalElements", page.getTotalElements());
            m.addAttribute("totalPages", page.getTotalPages());
            m.addAttribute("isFirst", page.isFirst());
            m.addAttribute("isLast", page.isLast());

        }
        return "/admin/orders";

    }

    @GetMapping("/add-admin")
    public String loadAdminAdd() {
        return "/admin/add_admin";
    }

    // Email availability check for admin/user forms
    @GetMapping(value = "/check-email", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> checkEmailExists(@RequestParam("email") String email) {
        UserDtls existing = userService.getUserByEmail(email);
        boolean exists = existing != null;
        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        return result;
    }

    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
            throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        UserDtls saveUser = userService.saveAdmin(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());

//				System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Register successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/admin/add-admin";
    }

    @GetMapping("/profile")
    public String profile() {
        return "/admin/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
        UserDtls updateUserProfile = userService.updateUserProfile(user, img);
        if (ObjectUtils.isEmpty(updateUserProfile)) {
            session.setAttribute("errorMsg", "Profile not updated");
        } else {
            session.setAttribute("succMsg", "Profile Updated");
        }
        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
                                 HttpSession session) {
        UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

        if (matches) {
            String encodePassword = passwordEncoder.encode(newPassword);
            loggedInUserDetails.setPassword(encodePassword);
            UserDtls updateUser = userService.updateUser(loggedInUserDetails);
            if (ObjectUtils.isEmpty(updateUser)) {
                session.setAttribute("errorMsg", "Password not updated !! Error in server");
            } else {
                session.setAttribute("succMsg", "Password Updated sucessfully");
            }
        } else {
            session.setAttribute("errorMsg", "Current Password incorrect");
        }

        return "redirect:/admin/profile";
    }


    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());

        Iterator<Cart> iterator = carts.iterator();
        while (iterator.hasNext()) {
            Cart cart = iterator.next();
            if (cart.getProduct().getStock() == 0) {
                cartService.deleteCart(cart.getId());
                iterator.remove();
            } else if (cart.getQuantity() > cart.getProduct().getStock()) {
                cart.setQuantity(cart.getProduct().getStock());
                cartService.updateCart(cart);
            }
        }

        m.addAttribute("carts", carts);
        if (!carts.isEmpty()) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/admin/cart";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        Cart saveCart = cartService.saveCart(pid, uid);

        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "This product has reached the maximum in the cart");
        } else {
            session.setAttribute("succMsg", "Product added to cart");
        }
        return "redirect:/product/" + pid;
    }

    //Update Admin by BaoThong
    @GetMapping("/ordersAdmin")
    public String orderPage(Principal p, Model m) {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        m.addAttribute("carts", carts);
        UserDtls userDtls = userService.getUserByEmail(user.getEmail());
        m.addAttribute("userDtls", userDtls);
        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrder = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double tax = totalOrder * 0.05;
            Double totalOrderPrice = totalOrder + tax + 250;
            m.addAttribute("orderPrice", orderPrice);
            m.addAttribute("tax", tax);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/admin/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws Exception {
        // System.out.println(request);
        UserDtls user = getLoggedInUserDetails(p);
        orderService.saveOrder(user.getId(), request);

        return "redirect:/admin/success";
    }

    @GetMapping("/success")
    public String loadSuccess() {
        return "/admin/success";
    }

    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p) {
        UserDtls loginUser = getLoggedInUserDetails(p);
        List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
        m.addAttribute("orders", orders);
        return "/admin/my_orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatusAdmin(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

        try {
            commonUtil.sendMailForUpdate(updateOrder, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "status not updated");
        }
        return "redirect:/admin/user-orders";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/admin/cart";
    }

    @GetMapping("/cartDeleteProduct")
    public String cartDeleteProduct(@RequestParam Integer cid) {
        cartService.deleteCart(cid);
        return "redirect:/admin/cart";
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        // Add necessary attributes to the model
        return "admin/statistics"; // Ensure this matches the view name
    }

    @GetMapping("/statistics_year")
    public String showStatisticsYear(Model model) {
        // Add necessary attributes to the model
        return "admin/statistics_year";
    }

    @GetMapping("/statistics_year/{year}")
    @ResponseBody
    public Map<String, Object> showStatistics(@PathVariable int year) {
        List<Object[]> salesData = orderService.getMonthlySalesByYear(year);
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        int[] prices = new int[12];

        for (Object[] data : salesData) {
            int month = (int) data[0];
            Number totalPrice = (Number) data[1];
            prices[month - 1] = totalPrice.intValue();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("months", months);
        response.put("prices", prices);

        return response;
    }

    //TK theo Day
    @GetMapping("/statistics_month")
    public String showStatisticsMonth(Model model) {
        // Add necessary attributes to the model
        return "admin/statistics_month"; // Ensure this matches the view name
    }

    @GetMapping("/statistics_month/{year}/{month}")
    @ResponseBody
    public Map<String, Object> getMonthlyStatistics(@PathVariable int year, @PathVariable int month) {
        List<Object[]> salesData = orderService.getDailySalesByMonth(year, month);
        Map<String, Object> response = new HashMap<>();
        response.put("days", salesData.stream().map(data -> data[0]).toArray());
        response.put("prices", salesData.stream().map(data -> data[1]).toArray());
        return response;
    }

    //TK by KH
    @GetMapping("/statistics_customer")
    public String showStatisticsCustomer(Model model) {
        // Add necessary attributes to the model
        return "admin/statistics_customer"; // Ensure this matches the view name
    }

    @GetMapping("/top_customers")
    @ResponseBody
    public List<Object[]> getTopCustomers(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        return orderService.getTopCustomersByDateRange(startDateTime, endDateTime);
    }

    //TK by SP
    @GetMapping("/statistics_product")
    public String showStatisticsProduct(Model model) {
        // Add necessary attributes to the model
        return "admin/statistics_product"; // Ensure this matches the view name
    }

    @GetMapping("/top_products")
    @ResponseBody
    public List<Object[]> getTopProducts(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        return orderService.getTopProductsByDateRange(startDateTime, endDateTime);
    }

}