package com.jianzhang.repository;

import com.jianzhang.entity.Game;
import com.jianzhang.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
}
