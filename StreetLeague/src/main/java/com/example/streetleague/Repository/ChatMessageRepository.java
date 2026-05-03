package com.example.streetleague.Repository;

import com.example.streetleague.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByContratSponsorIdOrderByTimestampAsc(Long contratId);
}
