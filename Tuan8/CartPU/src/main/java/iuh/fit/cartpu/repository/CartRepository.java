package iuh.fit.cartpu.repository;

import iuh.fit.cartpu.entity.UserCart;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<UserCart, String> {
}
