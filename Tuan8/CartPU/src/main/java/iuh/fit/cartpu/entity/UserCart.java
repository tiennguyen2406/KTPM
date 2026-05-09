package iuh.fit.cartpu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("carts") // Lưu vào không gian "carts" trong Redis
public class UserCart implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String userId;
    private List<CartItem> items = new ArrayList<>();
}
