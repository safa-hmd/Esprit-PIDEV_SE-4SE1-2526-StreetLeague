package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Post;
import com.example.streetleague.dto.postDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
    SELECT new com.example.streetleague.dto.postDTO(
        p.id, p.title, p.description, p.category,
        p.user.idUser, p.imageUrl, p.user.fullName,
        COUNT(c.id), CAST(p.likes AS long)
    )
    FROM Post p
    LEFT JOIN p.comments c
    GROUP BY p.id, p.title, p.description, p.category,
             p.user.idUser, p.imageUrl, p.user.fullName, p.likes
    ORDER BY COUNT(c.id) DESC
    LIMIT 1
""")
    Optional<postDTO> findMostCommentedPost();

    @Query("""
SELECT new com.example.streetleague.dto.postDTO(
        p.id, p.title, p.description, p.category,
        p.user.idUser, p.imageUrl, p.user.fullName,
        COUNT(c.id), CAST(p.likes AS long)
    )
    FROM Post p
    LEFT JOIN p.comments c
    GROUP BY p.id, p.title, p.description, p.category,
             p.user.idUser, p.imageUrl, p.user.fullName, p.likes
    ORDER BY p.likes DESC
    LIMIT 1
""")
    Optional<postDTO> findMostLikedPost();


    // ← ZID HATHA: Advanced Search
    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))")
    Page<Post> searchPosts(
            @Param("keyword")  String keyword,
            @Param("category") String category,
            Pageable pageable
    );
}
