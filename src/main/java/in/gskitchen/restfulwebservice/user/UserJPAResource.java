package in.gskitchen.restfulwebservice.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class UserJPAResource {

    @Autowired
    private UserRepository userRepository;
    //[^ !?.]

    @Autowired
    private PostRepositiry postRepositiry;

    @GetMapping("/jpa/users")
    public List<User> retrieveAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping(value = "/jpa/users/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Resource<User> retrieveUser(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()) throw new UserNotFoundException("id - "+id);

        //HATEOAS
        Resource<User> resource = new Resource<User>(user.get());
        ControllerLinkBuilder linkTo = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));
        return resource;
    }

    @DeleteMapping("/jpa/users/{id}")
    public void deleteUser(@PathVariable int id){
        userRepository.deleteById(id);
        //if(user == null) throw new UserNotFoundException("id - " + id);
    }
    
    @PostMapping("/jpa/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user){
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    //retreive posts by user
    @GetMapping("/jpa/users/{id}/posts")
    public List<Post> retrieveAllPosts(@PathVariable int id){
        Optional<User> userOptional = userRepository.findById(id);

        if(!userOptional.isPresent()){
            throw new UserNotFoundException(("id "+id));
        }

        return userOptional.get().getPosts();
    }

    @PostMapping("/jpa/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post){
        Optional<User> userOptional = userRepository.findById(id);

        if(!userOptional.isPresent()){
            throw new UserNotFoundException(("id "+id));
        }

        User user = userOptional.get();
        post.setUser(user);
        postRepositiry.save(post);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/jpa/posts")
    public List<Post> retrieveAllPost(){
        return postRepositiry.findAll();
    }

    @GetMapping(value = "/jpa/posts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Resource<Post> retrievePost(@PathVariable int id){
        Optional<Post> post = postRepositiry.findById(id);
        if(!post.isPresent()){
            throw new PostNotFoundException("id " + id);
        }

        Resource<Post> postResource = new Resource<Post>(post.get());

        return postResource;
    }


    //Set filter
    private MappingJacksonValue getFilteredValue(Object object, Set<String> fieldSet, String filterName){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(object);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(fieldSet);
        FilterProvider filters = new SimpleFilterProvider().addFilter(filterName, filter);
        mappingJacksonValue.setFilters(filters);

        return mappingJacksonValue;
    }
}
