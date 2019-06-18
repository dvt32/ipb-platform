package com.ipb.platform.persistance;

import com.ipb.platform.persistance.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    /**
     *
     * @param parentId  parent id to which we want to get all children categories
     * @return list of all categories
     */
    List<CategoryEntity> findAllByParentId(Long parentId);
}
