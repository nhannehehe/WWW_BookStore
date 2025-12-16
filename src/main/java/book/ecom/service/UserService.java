package book.ecom.service;

import book.ecom.model.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

	public UserDtls saveUser(UserDtls user);

	public UserDtls getUserByEmail(String email);

	public List<UserDtls> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);

	public void increaseFailedAttempt(UserDtls user);

	public void userAccountLock(UserDtls user);

	public boolean unlockAccountTimeExpired(UserDtls user);

	public void resetAttempt(int userId);

	public void updateUserResetToken(String email, String resetToken);

	public UserDtls getUserByToken(String token);

	public UserDtls updateUser(UserDtls user);

	public UserDtls updateUserProfile(UserDtls user, MultipartFile img);

	public UserDtls saveAdmin(UserDtls user);

	public Boolean existsEmail(String email);

	List<UserDtls> getAllUsers();

	void resetAttempt(Integer userId);

	UserDtls getUserByResetToken(String token);

	void clearResetToken(UserDtls user);

	boolean updatePassword(UserDtls user, String newPassword); 

	String encodePassword(String plainPassword); 

	public UserDtls getUserById(Integer id);

	public Boolean deleteUser(Integer id);
}
