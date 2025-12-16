package book.ecom.repository;

import book.ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByIsActiveTrue();

    Page<Product> findByIsActiveTrue(Pageable pageable);

    List<Product> findByCategory_Name(String category);

    List<Product> findByTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(String ch, String ch2);

    Page<Product> findByCategory_Name(Pageable pageable, String category);

    Page<Product> findByTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(String ch, String ch2,
                                                                                     Pageable pageable);

    Page<Product> findByisActiveTrueAndTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(String ch, String ch2,
                                                                                                    Pageable pageable);
    
    // Đếm số products thuộc category
    long countByCategory_Id(Integer categoryId);
}
