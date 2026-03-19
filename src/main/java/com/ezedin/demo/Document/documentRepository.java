package com.ezedin.demo.Document;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface documentRepository extends JpaRepository<document, Long> {
	boolean existsByIdAndOwnerId(Long id, Long ownerId);
	List<document> findAllByOwnerIdOrderByIdDesc(Long ownerId);
}
