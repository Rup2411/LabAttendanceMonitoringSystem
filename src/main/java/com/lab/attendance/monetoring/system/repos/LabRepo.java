package com.lab.attendance.monetoring.system.repos;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab.attendance.monetoring.system.entities.LabEntity;

public interface LabRepo extends JpaRepository<LabEntity, Long> {

	@Query(value = "select count(*) from lab_entity where lab_code = :x AND is_active = true", nativeQuery = true)
	int labCodeCount(@Param("x") String labCode);

	@Query(value = "select count(*) from lab_entity where lab_name = :x AND is_active = true", nativeQuery = true)
	int labNameCount(@Param("x") String labName);

	@Query(value = "select * from lab_entity where lab_code = :x", nativeQuery = true)
	Optional<LabEntity> findByLabCode(@Param("x") String labCode);
	
	@Query(value = "select * from lab_entity order by is_active desc", nativeQuery = true)
	List<LabEntity> findAllOrderByStatus();

	@Query(value = "select u.* FROM lab_entity u WHERE u.lab_code IN :x AND is_active = true", nativeQuery = true)
	List<LabEntity> findByLabIn(@Param("x") Set<String> enrolledLabCodes);

	@Query(value = "select * from lab_entity where lab_code = :x AND is_active = true", nativeQuery = true)
	List<LabEntity> findAllByLabCode(@Param("x") String labCode);

	@Query(value = """
			select a.* from lab_entity a
			INNER JOIN
			lab_entity_sessions b ON a.id = b.lab_entity_id
			WHERE b.session_id = :x
			""", nativeQuery = true)
	Optional<LabEntity> findByLabSessionId(@Param("x")String labSessionId);

	@Query(value = "select * from lab_entity where is_active = true", nativeQuery = true)
	List<LabEntity> findAllActiveLabs();
	
	@Query(value = "select * from lab_entity where is_active = false", nativeQuery = true)
	List<LabEntity> findAllInActiveLabs();

	@Query("SELECT l FROM LabEntity l WHERE l.labCode IN :labCodes")
    List<LabEntity> findAllByLabCodeIn(@Param("labCodes") Set<String> labCodes);

}
