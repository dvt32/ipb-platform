package com.ipb.platform.persistence.entities;

import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

//	postgres VARCHAR limit 10485760
	@Column(columnDefinition="VARCHAR(10485760)")
	private String description;
	
	private double latitude;
	
	private double longitude;
	
	@Enumerated(EnumType.STRING)
	private ObjectType type;

	@Column(name="is_approved")
	private boolean isApproved;
	
	@OneToMany(mappedBy = "object", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<ImageEntity> images;
	
	@ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE,
            })
    @JoinTable(name = "object_categories",
            joinColumns = @JoinColumn(name = "object_id",  referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id",  referencedColumnName = "id"))
	private List<CategoryEntity> categories;

	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			},
			mappedBy = "objects"
	)
	private List<RouteEntity> routes;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
//	@ManyToOne()
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private UserEntity creator;
}
