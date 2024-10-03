package com.bstirbat.personalitytest.repository;

import com.bstirbat.personalitytest.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantRepository extends JpaRepository<Variant, Long> {
}
