package in.gskitchen.restfulwebservice.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepositiry extends JpaRepository<Post, Integer> {
}
