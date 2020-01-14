package all.that.matters.repo;

import all.that.matters.model.Food;
import all.that.matters.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {
    @Query(value = "SELECT id, name, calories, user_id FROM food WHERE user_id = ?1", nativeQuery = true)
    List<Food> findByOwner(Long userId);

    Optional<Food> findById(Long id);

    @Query(value = "SELECT id, name, calories, user_id from food WHERE user_id IS NULL", nativeQuery = true)
    List<Food> findAllCommon();

    Optional<Food> findByNameAndOwner(String name, User user);

    @Query(value = "SELECT id, name, calories, user_id from food WHERE user_id IS NULL AND name NOT IN "
                           + "(SELECT name FROM food WHERE user_id = ?1)",
            nativeQuery = true)
    List<Food> findAllCommonExcludingPersonalByUserId(Long userId);

    void removeByFoodNameAndUserId(String foodName, Long userId);

}
