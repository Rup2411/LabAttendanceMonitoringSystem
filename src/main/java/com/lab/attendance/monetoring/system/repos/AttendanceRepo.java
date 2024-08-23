package com.lab.attendance.monetoring.system.repos;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab.attendance.monetoring.system.entities.AttendanceEntity;

public interface AttendanceRepo extends JpaRepository<AttendanceEntity, Long> {

	@Query(value = """
			select b.name,
			 b.department,
			 b.mobile_number as mobileNumber,
			 a.student_roll_no as studentRollNo
			 from
			 attendance_entity a
			 INNER JOIN
			 user_entity b ON a.student_roll_no = b.roll_no
			 WHERE lab_session_id = :y AND is_present = true
			""", nativeQuery = true)
	List<Map<String, String>> findAllPresentStudents(@Param("y") String labSessionId);

	@Query(value = """
			select b.name,
			 b.department,
			 b.mobile_number as mobileNumber,
			 a.student_roll_no as studentRollNo
			 from
			 attendance_entity a
			 INNER JOIN
			 user_entity b ON a.student_roll_no = b.roll_no
			 WHERE lab_session_id = :y AND is_present = false
			""", nativeQuery = true)
	List<Map<String, String>> findAllAbsentStudents(@Param("y") String labSessionId);

}
