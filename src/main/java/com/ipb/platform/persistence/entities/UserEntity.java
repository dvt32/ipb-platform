package com.ipb.platform.persistence.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ipb.platform.validation.PasswordMatches;
import com.ipb.platform.validation.ValidEmail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.List;

/**
 * This class represents a "user" entity in the database.
 * Each user has a type, which can either be "USER" or "ADMIN".
 * 
 * @author dvt32
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name="Users")
@PasswordMatches
public class UserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	private Long id;
	
	@NotBlank(message = "User e-mail must not be null or blank!")
	@ValidEmail
	@Column(length = 100)
	@Size(max = 100)
	private String email;
	
	@NotBlank(message = "User password must not be null or blank!")
	@Column(length = 100)
	@Size(min = 6, message = "User password must be at least 6 characters!")
	private String password;
	private String matchingPassword;
	
	@NotBlank(message = "First name must not be null or blank!")
	@Column(length = 255)
	@Size(max = 255)
	private String firstName;
	
	@NotBlank(message = "Last name must not be null or blank!")
	@Column(length = 255)
	@Size(max = 255)
	private String lastName;
	
	@NotNull(message = "Birth date must not be be null!")
    private java.sql.Date birthday;
	
	@Enumerated(EnumType.STRING)
	private UserType type;


	@OneToMany(mappedBy = "creator")
	private List<RouteEntity> routes;

	@OneToMany(mappedBy = "creator")
	private List<ObjectEntity> objects;

	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE,
			})
	@JoinTable(name = "user_categories",
			joinColumns = @JoinColumn(name = "user_id",  referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "category_id",  referencedColumnName = "id"))
	private List<CategoryEntity> categories;
	
}
