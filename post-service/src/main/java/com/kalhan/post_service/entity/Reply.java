package com.kalhan.post_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Data
@NoArgsConstructor
@Table(name = "replies")
public class Reply {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    private String userId;
    private String replyContent;
}
