package org.hanghae99.fcfs.bookmark.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "bookMarks")
public class BookMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_mark_id")
    private Long id;

    @Column(name = "book_user_id")
    private Long bookUserId;

    @Column(name = "book_product_id")
    private Long bookProductId;

    public BookMark(Long userId, Long productId) {
        this.bookUserId = userId;
        this.bookProductId = productId;
    }
}
