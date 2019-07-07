package com.ipb.platform.persistence;

import com.ipb.platform.persistence.entities.CategoryEntity;
import com.ipb.platform.persistence.entities.QCategoryEntity;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findAllByParentId(Long parentId);

    List<CategoryEntity> findAllByName(String name);

}
