package com.lab.attendance.monetoring.system.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab.attendance.monetoring.system.entities.StudentAbsenceCount;

public interface StudentAbscenceCountRepo extends JpaRepository<StudentAbsenceCount, Long> {

	@Query(value = "select absent_count from student_absence_count where student_roll_no = :x", nativeQuery = true)
	int absentCount(@Param("x") String rollNo);
	
	@Query(value = "select * from student_absence_count where student_roll_no = :x", nativeQuery = true)
	Optional<StudentAbsenceCount> absentCountByRollNo(@Param("x") String rollNo);
}
