package com.ipb.platform.persistence;

import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;
import com.ipb.platform.persistence.entities.QObjectEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectRepository extends JpaRepository<ObjectEntity, Long>, QuerydslPredicateExecutor<ObjectEntity> {
	default Page<ObjectEntity> findAllAroundCoordinates(
			Pageable pageable,
			double latitude,
			double longitude,
			ObjectType type
	) {
		QObjectEntity objectEntity = QObjectEntity.objectEntity;

		NumberExpression<Double> param1 = (objectEntity.latitude.subtract(latitude)).multiply(69.1);

		NumberExpression<Double> longitudeNumberExpression = objectEntity.longitude.multiply(0).add(longitude);
		NumberExpression<Double> param2 =
				longitudeNumberExpression
						.subtract(objectEntity.longitude)
						.multiply( Math.cos(latitude / 57.3))
						.multiply(69.1);

		int miles = 25;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(param1.multiply(param1).add(param2.multiply(param2)).sqrt().lt(miles));
		builder.and(objectEntity.type.eq(type));
		return findAll(builder, pageable);
	}

	List<ObjectEntity> findAllByNameContainsOrDescriptionContains(String name, String description, Pageable pageable);

}
