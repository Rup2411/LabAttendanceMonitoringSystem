package com.lab.attendance.monetoring.system.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab.attendance.monetoring.system.entities.FineEntity;

public interface FineRepo extends JpaRepository<FineEntity, Long> {

	
	@Query(value = "select * from fine_entity where student_roll_no = :x", nativeQuery = true)
	Optional<FineEntity> getByStudentRollNo(@Param("x") String rollNo);

}
