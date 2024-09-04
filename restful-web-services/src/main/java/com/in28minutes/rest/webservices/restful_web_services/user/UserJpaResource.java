package com.in28minutes.rest.webservices.restful_web_services.user;

import java.net.URI;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.in28minutes.rest.webservices.restful_web_services.jpa.PostRepository;
import com.in28minutes.rest.webservices.restful_web_services.jpa.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {
	
	private UserDaoService service;
	private UserRepository repository;
	
	private PostRepository postRepository;
	
	public UserJpaResource(UserRepository repository,PostRepository postRepository) {
		super();
		this.postRepository=postRepository;
		this.repository=repository;
	}
	
	@GetMapping(path="/jpa/users")
	public List<User> retrieveAllUsers(){
		return repository.findAll();
	}
	
	
	@GetMapping(path="/jpa/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable Integer id){
		
		Optional<User> user=repository.findById(id);
		if(user.isEmpty()) {
			throw new UserNotFoundException("id:"+id) ;
		}
		
		EntityModel<User> entityModel =EntityModel.of(user.get());
		
		WebMvcLinkBuilder link= linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-users"));
		return entityModel ;
	}
	
	
	@DeleteMapping(path="/jpa/users/{id}")
	public void deleteUser(@PathVariable Integer id){
		
		repository.deleteById(id);
	}
	
	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		
		 User savedUser=repository.save(user);
		
		URI location=ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();
		
	}
	
	@GetMapping(path="/jpa/users/{id}/posts")
	public List<Post> retrivePostsOfUser(@PathVariable Integer id){
		
		Optional<User> user=repository.findById(id);
		if(user.isEmpty()) {
			throw new UserNotFoundException("id:"+id) ;
		}
		
		return user.get().getPosts();
	}
	
	@PostMapping(path="/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPostsOfUser(@PathVariable Integer id,@Valid @RequestBody Post post){
		
		Optional<User> user=repository.findById(id);
		if(user.isEmpty()) {
			throw new UserNotFoundException("id:"+id) ;
		}
	            post.setUser(user.get());
		        Post savedPost=  postRepository.save(post);
		        URI location=ServletUriComponentsBuilder.fromCurrentRequest()
						.path("/{id}")
						.buildAndExpand(savedPost.getId())
						.toUri();
				
				return ResponseEntity.created(location).build();
	}
	
	
	
	

	
}
