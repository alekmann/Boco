import http from "./http";

export const addPost = (post) => {
  return http.post("/posts", post);
};

export const editPost = (post) => {
  return http.put("/posts", post);
};

export const deletePost = (id) => {
  return http.delete("/posts/" + id);
};

export const addPicture = (picture, postId) => {
  return http.post("/pictures/" + postId, picture);
};

export const getPost = (id) => {
  return http.get(`/posts/${id}`);
};

export const getPostPictures = (id) => {
  return http.get(`/pictures/${id}`);
};

/**
 * Fetches a list of posts with the given length
 *
 * @param {number} index page in pagination
 * @param {number} size amount of posts to get per page
 * @returns list of posts
 */
export const getPaginatedPosts = (index, size) => {
  return http.get("/posts/" + index + "/" + size);
};

/**
 * Fetches the posts for a given user
 *
 * @param {number} id id of user to fetch posts for
 * @param {number} index page in pagination
 * @param {number} size amount of posts to fetch per page
 * @returns list of posts
 */
export const getUserPosts = (id, index, size) => {
  return http.get(`/posts/user/${id}/${index}/${size}`);
};

/**
 * Fetches the posts for a given organization
 *
 * @param {number} orgNum organization number of organization to fetch posts for
 * @param {number} index page in pagination
 * @param {number} size amount of posts to fetch per page
 * @returns list of posts
 */
export const getOrganizationPosts = (orgNum, index, size) => {
  return http.get(`/posts/org/${orgNum}/${index}/${size}`);
};

export const search = (index, size, searchRequest) => {
  // Prep Title
  const search = searchRequest.title ? `&search=${searchRequest.title}` : "";

  // Prep location
  const location = searchRequest.location
    ? `&lat=${searchRequest.location.lat}` +
      `&lon=${searchRequest.location.lng}` +
      `&range=${searchRequest.location.radius}`
    : "";

  // Prep category
  const category = searchRequest.category
    ? `&category=${searchRequest.category.name}`
    : "";

  // Prep dates
  const dateRange =
    searchRequest.dates && searchRequest.dates.start && searchRequest.dates.end
      ? `&from=${searchRequest.dates.start.toISOString().substring(0, 10)}` +
        `&to=${searchRequest.dates.end.toISOString().substring(0, 10)}`
      : "";

  // Execute request
  return http.get(
    `/posts/search?index=${index}&size=${size}` +
      search +
      location +
      category +
      dateRange
  );
};
