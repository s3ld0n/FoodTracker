package org.training.food_tracker.repo;

import org.springframework.transaction.annotation.Transactional;
import org.training.food_tracker.model.Food;
import org.training.food_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {
    List<Food> findAllByOwnerOrderByIdDesc(User user);

    Optional<Food> findById(Long id);

    @Query(value = "SELECT id, name, calories, user_id from food WHERE user_id IS NULL ORDER BY id DESC", nativeQuery = true)
    List<Food> findAllCommon();

    Optional<Food> findByNameAndOwner(String name, User user);

    @Query(value = "SELECT id, name, calories, user_id from food WHERE user_id IS NULL AND name NOT IN "
                           + "(SELECT name FROM food WHERE user_id = ?1) ORDER BY id DESC",
            nativeQuery = true)
    List<Food> findAllCommonExcludingPersonalByUserId(Long userId);

    @Transactional
    void removeByNameAndOwner(String foodName, User user);

}
