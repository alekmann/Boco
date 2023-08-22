package no.boco.backend.post;

import no.boco.backend.category.CategoryService;
import no.boco.backend.organization.Organization;
import no.boco.backend.organization.OrganizationService;
import no.boco.backend.user.User;
import no.boco.backend.user.UserService;
import no.boco.backend.userorg.OrganizationRole;
import no.boco.backend.userorg.UserOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
public class PostService{
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private OrganizationService orgService;
    @Autowired
    private CategoryService categoryService;


    Logger logger = LoggerFactory.getLogger(PostService.class);

    /**
     * Save a new Post to the database.
     * @param post the Post to save.
     * @param user the User calling this method.
     * @return the Id of the created Post.
     * @throws NoSuchElementException
     * @throws IllegalAccessException
     */
    public Long add(Post post, User user) throws NoSuchElementException, IllegalAccessException {
        if(user.getId() == null || !userService.userExists(user.getId())){
            logger.trace("No user found");
            throw new NoSuchElementException("Could not find user");
        }
        if(post.getCategory().getId() == null || !categoryService.categoryExists(post.getCategory().getId())){
            logger.trace("No category found");
            throw new NoSuchElementException("Could not find category");
        }else{
            post.setCategory(categoryService.getByName(post.getCategory().getName()));
        }
        if(post.getOrganization() != null){
            Organization org = post.getOrganization();
            if(!orgService.organizationExists(org.getOrgNum())) {
                throw new NoSuchElementException("Could not find organization");
            }
            if(isEmployee(user, org)){
                return savePost(post, user);
            }

            throw new IllegalAccessException("User is not an employee in the Organization");
        }

        return savePost(post, user);
    }

    /**
     * Sets the created date of a post to now and saves it to the database.
     * @param post post to be saved
     * @param user user creating the post
     * @return post id of the new post
     */
    private Long savePost(Post post, User user){
        post.setUser(user);
        post.setCreatedDate(Date.valueOf(LocalDate.now()));

        return postRepository.save(post).getPostId();
    }

    /**
     * Checks if a given user is admin of a given organization.
     * @param user user
     * @param org orgnization
     * @return boolean representing whether the user is admin of the organization
     */
    private boolean isAdmin(User user, Organization org){
        Set<UserOrganization> userOrganizations = org.getUserOrganizations();
        for(UserOrganization userOrg : userOrganizations){
            if(userOrg.getUser().equals(user) && userOrg.getOrganizationRole().equals(OrganizationRole.ADMIN)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given user is an employee of a given organization.
     * @param user user
     * @param org organization
     * @return boolean representing whether the user is an employee of the organization
     */
    private boolean isEmployee(User user, Organization org){
        Set<UserOrganization> userOrganizations = user.getUserOrganizations();
        for(UserOrganization userOrg : userOrganizations){
            if(userOrg.getUser().equals(user)){
                return true;
            }
        }
        return false;
    }

    /**
     * Edit an existing post.
     * @param newPost the edited post object.
     * @param user the user calling this method.
     * @throws IllegalAccessException
     * @throws NoSuchElementException
     * @return
     */
    public Post edit(Post newPost, User user) throws IllegalAccessException, NoSuchElementException{
        //Check if post exists
        if(newPost.getPostId() == null) throw new NoSuchElementException("PostId must not be null");
        Optional<Post> postOpt = postRepository.findById(newPost.getPostId());
        if(postOpt.isEmpty()) {
            throw new NoSuchElementException("Could not find a Post with the given Id");
        }
        Post oldPost = postOpt.get();

        //Check if the caller owns the post
        if(!user.equals(oldPost.getUser())){
            //Check if the caller is the admin of the organization that owns the post
            Organization org = oldPost.getOrganization();
            if(org != null) {
                if(!isAdmin(user, org)) {
                    throw new IllegalAccessException("Can not edit a post from another user");
                }
            }else{
                throw new IllegalAccessException("Can not edit a post from another user");
            }
        }
        //Check if category exists
        if(newPost.getCategory() == null || newPost.getCategory().getId() == null || !categoryService.categoryExists(newPost.getCategory().getId())){
            throw new NoSuchElementException("Could not find category");
        }

        oldPost.setTitle(newPost.getTitle());
        oldPost.setPricePerDay(newPost.getPricePerDay());
        oldPost.setCategory(newPost.getCategory());
        oldPost.setDescription(newPost.getDescription());
        oldPost.setLocation(newPost.getLocation());
        oldPost.setPictures(newPost.getPictures());
        oldPost.setInventory(newPost.getInventory());

        return postRepository.save(oldPost);
    }

    /**
     * Delete a Post from the database.
     * @param postId the Id of the Post.
     * @param user the User calling this method
     * @throws NoSuchElementException if a Post with the given Id is not found.
     * @throws IllegalAccessException if the User calling this method does not own the Post.
     */
    public void delete(Long postId, User user) throws NoSuchElementException, IllegalAccessException {
        //Check if post exists
        Optional<Post> postOpt = postRepository.findById(postId);
        if(postOpt.isEmpty()) {
            throw new NoSuchElementException("Could not find a Post with the given Id");
        }

        //Check if the user calling owns the post
        Post post = postOpt.get();
        if(post.getUser().equals(user)) {
            postRepository.delete(post);
            return;
        }

        //Check if the user is an admin of the organization the Post belongs to
        Organization org = post.getOrganization();
        if(org != null){
            if(isAdmin(user, org)){
                postRepository.delete(post);
                return;
            }
        }

        throw new IllegalAccessException("Can not delete a post from another user");
    }

    /**
     * Returns a post based on post id.
     * @param postId of the post.
     * @return requested post.
     * @throws PostNotFoundException
     */
    public Post findPostById(Long postId){
        Optional<Post> test = postRepository.findById(postId);
        return postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Couldn't find post " + postId));
    }

    /**
     * Get all posts by a User. Can be called to se your own or someone elses posts.
     * @param page_index the current page.
     * @param page_size Posts per page.
     * @param userId the id of the User.
     * @return a Page of all posts found.
     * @throws IllegalArgumentException
     * @throws NoSuchElementException if the User could not be found.
     */
    public Page<Post> getByUser(int page_index, int page_size, Long userId) throws IllegalArgumentException, NoSuchElementException{
        validatePageArgs(page_index, page_size);

        if(userService.userExists(userId)) {
            Pageable pageable = PageRequest.of(page_index, page_size);
            return postRepository.findPostsByUser_IdAndOrganizationIsNull(userId, pageable);
        }else{
            throw new NoSuchElementException("The user could not be found");
        }
    }

    /**
     * Get all posts by an Organization.
     * @param page_index the current page.
     * @param page_size Posts per page.
     * @param orgNum the organization number of the Organization.
     * @return a Page of all posts found.
     * @throws IllegalArgumentException
     * @throws NoSuchElementException if the Organization could not be found.
     */
    public Page<Post> getByOrganization(int page_index, int page_size, Long orgNum) throws IllegalArgumentException, NoSuchElementException{
        validatePageArgs(page_index, page_size);

        if(orgService.organizationExists(orgNum)) {
            Pageable pageable = PageRequest.of(page_index, page_size);
            return postRepository.findPostsByOrganization_OrgNum(orgNum, pageable);
        }else{
            throw new NoSuchElementException("The organization could not be found");
        }
    }

    /**
     * Get all posts without searching/filtering. Returns in pages defined by the current page index
     * and page size.
     * @param page_index the current page.
     * @param page_size how many Posts per page.
     * @return a Page of all posts found.
     * @throws IllegalArgumentException
     */
    public Page<Post> getPosts(int page_index, int page_size){
        validatePageArgs(page_index, page_size);
        Pageable pageable = PageRequest.of(page_index, page_size);

        return postRepository.findAll(pageable);
    }

    /**
     * Validates pagination arguments
     * @param page_index requested page index
     * @param page_size requested size of page
     */
    private void validatePageArgs(int page_index, int page_size){
        if(page_index < 0 || page_size <= 0){
            throw new IllegalArgumentException("Integer can not be 0 or less");
        }
    }

    /**
     * Searches for posts with the given parameters.
     * @param queryParams map of key/value pairs containing search data
     * @return list of posts based on the seach query and pagination paramaters
     * @throws IllegalArgumentException if there is invalid pagination input
     */
    public Page<Post> search(Map<String, String> queryParams) throws IllegalArgumentException {
        Integer index;
        Integer size;
        String title;
        String category;
        Date from;
        Date to;
        Double latitude;
        Double longitude;
        Integer range;

        // Pagination
        try {
            index = Integer.parseInt(queryParams.get("index"));
            size = Integer.parseInt(queryParams.get("size"));
        } catch (Exception e) {
            index = 0;
            size = 15;
        }

        // title
        try {
            title = queryParams.get("search");
            title = title.toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            title = null;
        }
        try {
            category = queryParams.get("category");
            category = category.toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            category = null;
        }
        try {
            latitude = Double.parseDouble(queryParams.get("lat"));
            longitude = Double.parseDouble(queryParams.get("lon"));
            range = Integer.parseInt(queryParams.get("range"));
        } catch (Exception e) {
            latitude = null;
            longitude = null;
            range = null;
        }
        try {
            from = Date.valueOf(queryParams.get("from"));
            to = Date.valueOf(queryParams.get("to"));
        } catch (Exception e) {
            from = null;
            to = null;
        }


        validatePageArgs(index, size);
        Pageable pageable = PageRequest.of(index, size, Sort.by("postId"));

       return postRepository.searchForPosts(title, category, latitude, longitude, range, from , to, pageable);
    }

}
