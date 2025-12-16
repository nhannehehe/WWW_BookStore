package book.ecom.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import book.ecom.model.UserDtls;
import book.ecom.service.CartService;
import book.ecom.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthSucessHandlerImpl implements AuthenticationSuccessHandler {

	@Autowired
	private CartService cartService;

	@Autowired
	@Lazy
	private UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		
		Set<String> roles = AuthorityUtils.authorityListToSet(authorities);
		
		// Merge guest cart to user cart when user logs in
		String email = authentication.getName();
		UserDtls user = userService.getUserByEmail(email);
		if (user != null) {
			HttpSession session = request.getSession();
			cartService.mergeSessionCartToUserCart(user.getId(), session);
		}
		
		if(roles.contains("ROLE_ADMIN"))
		{
			response.sendRedirect("/admin/");
		}else {
			response.sendRedirect("/");
		}
		
	}

}
