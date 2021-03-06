package blog.controller;

import blog.common.CurrentUser;
import blog.form.CreatePost;
import blog.model.Category;
import blog.model.Post;
import blog.model.User;
import blog.services.PostService;
import blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @RequestMapping("posts")
    public String getAllPosts(Model model) {
        List<Post> list = postService.findAll();
        model.addAttribute("posts", list);
        return "posts";
    }

    @RequestMapping(value = "/allposts",produces="application/json", method = RequestMethod.GET)
    public ResponseEntity<List<Post>> readAllPosts() {
        List<Post> allposts = postService.findAll();
        if (allposts.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Post>>(allposts, HttpStatus.OK);
    }

    @RequestMapping(value = "/newpost", method = RequestMethod.POST, consumes ="application/json")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        postService.create(post);
        return new ResponseEntity<Post>(post, HttpStatus.OK);
    }

    @RequestMapping("/posts/create")
    public String createPost() {
        return "posts/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createPostPage(CreatePost createPost) {

        Post post = new Post();
        List<Category> categories = new ArrayList<Category>();

        post.setTitle(createPost.getTitle());
        post.setBody(createPost.getBody());

        if(createPost.getSpringBlog()!=null){
            Category category = new Category();
            category.setId(0);
            category.setCategoryType(createPost.getSpringBlog());
            categories.add(category);
        }

        if(createPost.getJavaBlog()!=null){
            Category category = new Category();
            category.setId(1);
            category.setCategoryType(createPost.getJavaBlog());
            categories.add(category);
        }

        post.setCategoryList(categories);
        User user = userService.getUserByName(CurrentUser.getInstance().getUserName());
        post.setUser(user);
        post.setId(System.currentTimeMillis()%1000);
        postService.create(post);
        return "redirect:/posts";
    }
}
