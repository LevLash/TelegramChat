package com.bot.repository;

import com.bot.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotConnectionRepository extends JpaRepository<Connection, Long> {
}
