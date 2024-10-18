package org.hanghae99.fcfs.likemark.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.product.entity.Product;
import org.hanghae99.fcfs.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "likeMarks")
public class LikeMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_mark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public LikeMark(User user, Product product) {
        this.user = user;
        this.product = product;
    }
}
