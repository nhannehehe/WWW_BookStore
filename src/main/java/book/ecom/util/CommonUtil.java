package book.ecom.util;

import book.ecom.model.ProductOrder;
import book.ecom.model.UserDtls;
import book.ecom.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("daspabitra55@gmail.com", "Shooping Cart");
        helper.setTo(reciepentEmail);

        String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
                + "\">Change my password</a></p>";
        helper.setSubject("Password Reset");
        helper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {

        // http://localhost:8080/forgot-password
        String siteUrl = request.getRequestURL().toString();

        return siteUrl.replace(request.getServletPath(), "");
    }

    String msg = null;
    ;

    public Boolean sendMailForUpdate(ProductOrder order, String status) throws Exception {

        msg = "<p>Hello [[name]],</p>"
                + "<p>Thank you order <b>[[orderStatus]]</b>.</p>"
                + "<p><b>Product Details:</b></p>"
                + "<p>Name : [[productName]]</p>"
                + "<p>Category : [[category]]</p>"
                + "<p>Quantity : [[quantity]]</p>"
                + "<p>Price : [[price]]</p>"
                + "<p>Payment Type : [[paymentType]]</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("daspabitra55@gmail.com", "Shooping Cart");
        helper.setTo(order.getOrderAddress().getEmail());

        msg = msg.replace("[[name]]", order.getOrderAddress().getFirstName());
        msg = msg.replace("[[orderStatus]]", status);
        msg = msg.replace("[[productName]]", order.getProduct().getTitle());
        msg = msg.replace("[[category]]", order.getProduct().getCategory().getName());
        msg = msg.replace("[[quantity]]", order.getQuantity().toString());
        msg = msg.replace("[[price]]", order.getPrice().toString());
        msg = msg.replace("[[paymentType]]", order.getPaymentType());

        helper.setSubject("Product Order Status");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }

    public Boolean sendMailForProductOrder(List<ProductOrder> orders, String status) throws Exception {
        StringBuilder msg = new StringBuilder();
        double total = 0.0;
        msg.append("<p>Hello [[name]],</p>")
                .append("<p>Thank you for your order. Your order status is <b>[[orderStatus]]</b>.</p>")
                .append("<p><b>Product Details:</b></p><br>");

        for (ProductOrder order : orders) {
            String productDetails = "<p><b>Name : [[productName]]<b></p>"
                    + "<p>Category : [[category]]</p>"
                    + "<p>Quantity : [[quantity]]</p>"
                    + "<p>Price : [[price]]</p>"
                    + "<p>Payment Type : [[paymentType]]</p>"
                    + "<br>";

            productDetails = productDetails.replace("[[productName]]", order.getProduct().getTitle());
            productDetails = productDetails.replace("[[category]]", order.getProduct().getCategory().getName());
            productDetails = productDetails.replace("[[quantity]]", order.getQuantity().toString());
            productDetails = productDetails.replace("[[price]]", order.getPrice().toString());
            productDetails = productDetails.replace("[[paymentType]]", order.getPaymentType());
            total += order.getPrice();
            msg.append(productDetails);
        }
        msg.append("<p><b>Total Price : [[total]]</b></p>");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("daspabitra55@gmail.com", "Shopping Cart");
        helper.setTo(orders.get(0).getOrderAddress().getEmail());

        msg = new StringBuilder(msg.toString().replace("[[name]]", orders.get(0).getOrderAddress().getFirstName()));
        msg = new StringBuilder(msg.toString().replace("[[orderStatus]]", status));
        msg = new StringBuilder(msg.toString().replace("[[total]]", String.valueOf(total)));

        helper.setSubject("Product Order Status");
        helper.setText(msg.toString(), true);
        mailSender.send(message);

        return true;
    }

    public UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }


}
