package com.spyneai.carmanagement.repository;

import com.spyneai.carmanagement.entity.Car;
import com.spyneai.carmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car,Long>
{
    List<Car> findAllByOwner(User owner);
    Page<Car> findAll(Pageable pageable);
    @Query("SELECT c FROM Car c WHERE :tag MEMBER OF c.tags")
    List<Car> findByTag(@Param("tag") String tag);

}
