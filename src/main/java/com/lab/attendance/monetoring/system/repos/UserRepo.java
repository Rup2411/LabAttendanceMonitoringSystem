package com.lab.attendance.monetoring.system.repos;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab.attendance.monetoring.system.entities.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity, Long> {

	@Query(value = "select * from user_entity where email = :x", nativeQuery = true)
	Optional<UserEntity> findByEmail(@Param("x") String username);

	@Query(value = "select COUNT(*) from user_entity where email = :x", nativeQuery = true)
	int countByEmail(@Param("x") String emailId);

	@Query(value = "select COUNT(*) from user_entity where mobile_number = :x", nativeQuery = true)
	int countByContactNumber(@Param("x") String contactNumber);

	@Query(value = "select COUNT(*) from user_entity where roll_no = :x", nativeQuery = true)
	int countByRollNo(@Param("x") Long rollNo);

	@Query(value = "SELECT image FROM user_entity WHERE roll_no = :x", nativeQuery = true)
	Optional<byte[]> findImageByRollNo(@Param("x") String rollNo);

	@Query(value = "select * from user_entity where role = 'STUDENT'", nativeQuery = true)
	List<UserEntity> getAllStudentUsers();

	@Query(value = "select * from user_entity where roll_no = :x", nativeQuery = true)
	Optional<UserEntity> findByRollNo(@Param("x") String rollNo);

	@Query(value = "SELECT u.* FROM user_entity u WHERE u.roll_no IN :x", nativeQuery = true)
	List<UserEntity> findByRollNoIn(@Param("x") Set<String> enrolledStudentEmails);
	
	@Query(value = "SELECT parent_email from user_entity where email = :x", nativeQuery = true)
	String getParentEmailByStudentEmail(@Param("x") String email);
	
	@Query(value = "SELECT local_guardian_email from user_entity where email = :x", nativeQuery = true)
	String getLocalGuardianEmailByStudentEmail(@Param("x") String email);
	
	@Query(value = "SELECT email from user_entity where roll_no = :x", nativeQuery = true)
	String getEmailByRollNo(@Param("x") String rollNo);
}
