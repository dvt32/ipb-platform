package com.ipb.platform.persistance.entities;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Max;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "objects")
@Inheritance(strategy = InheritanceType.JOINED)

public class ObjectEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "object_seq")
	private Long id;
	
	private String name;

//	@Length(max = 214748364 )
//	@Column(columnDefinition="VARCHAR(2147483647)")
//	postgres VARCHAR limit 10485760
	@Column(columnDefinition="VARCHAR(10485760)")
	private String description;
	
	private double latitude;
	
	private double longitude;
	
	@Enumerated(EnumType.STRING)
	private ObjectType type;
	
	@OneToMany(mappedBy = "object", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<ImageEntity> images;
	
	@ManyToMany(fetch = FetchType.LAZY)//,
//            cascade = {
//                CascadeType.PERSIST,
//                CascadeType.MERGE,
//            })
    @JoinTable(name = "object_categories",
            joinColumns = @JoinColumn(name = "object_id",  referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id",  referencedColumnName = "id"))
	private List<CategoryEntity> categories;

}
