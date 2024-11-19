package com.lab.attendance.monetoring.system.repos;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab.attendance.monetoring.system.entities.LabSessionEntity;

public interface LabSessionRepo extends JpaRepository<LabSessionEntity, Long> {

	@Query(value = "SELECT * FROM lab_session_entity WHERE session_location = :x AND lab_session_date = :y", nativeQuery = true)
	List<LabSessionEntity> findActiveSessionsByLabLocationAndDate(@Param("x")String sessionLocation,@Param("y") LocalDate labSessionDate);

	@Query(value = "SELECT * FROM lab_session_entity WHERE instructor_name = :x AND lab_session_date = :y", nativeQuery = true)
	List<LabSessionEntity> findActiveSessionsByInstructorAndDate(@Param("x")String instructorName,@Param("y") LocalDate labSessionDate);

	@Query(value = "SELECT * FROM lab_session_entity WHERE lab_session_id = :x", nativeQuery = true)
	Optional<LabSessionEntity> findByLabSessionId(@Param("x") String labSessionId);
	
	@Query(value = """
			select a.roll_no from user_entity a
			INNER JOIN
			user_entity_enrolled_lab_codes b ON a.id = b.user_entity_id
			INNER JOIN
			lab_session_entity c ON b.enrolled_labs = c.lab_code
			where c.lab_session_id = :x 
			""", nativeQuery = true)
	List<Long> findStudentRollNosByLabSessionId(@Param("x")String labSessionId);

	@Query(value = "SELECT * FROM lab_session_entity WHERE lab_code = :x", nativeQuery = true)
	List<LabSessionEntity> findAllByLabCode(@Param("x") String labCode);

	@Query(value = "select * from lab_session_entity order by session_status desc", nativeQuery = true)
	List<LabSessionEntity> findAllOrderByStatus();

}
