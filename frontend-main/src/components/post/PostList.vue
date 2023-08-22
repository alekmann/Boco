<template>
  <h2 id="no-posts" v-if="postsArray.length === 0">
    Her var det visst ingenting 😬
  </h2>
  <div v-else id="post-list">
    <PostCard v-for="post in this.postsArray" :key="post" :post="post" />
  </div>
  <Paginator
    v-show="this.totalPages > 1"
    @changePage="changePage"
    :totalPages="this.totalPages"
  />
</template>

<script>
import PostCard from "./PostCard.vue";
import Paginator from "./Paginator.vue";

export default {
  emits: ["changePage"],
  components: {
    PostCard,
    Paginator,
  },
  props: ["posts"],
  watch: {
    posts(newValue, oldValue) {
      this.postsArray = newValue.content;
      this.totalPages = newValue.totalPages;
    },
  },
  data() {
    return {
      postsArray:
        typeof this.posts.content === typeof [] ? this.posts.content : [],
      totalPages: this.posts.totalPages,
      firstLoad: false,
    };
  },
  beforeUpdate() {
    if (!this.firstLoad) {
      this.postsArray = this.posts.content;
      this.totalPages = this.posts.totalPages;
      this.firstLoad = true;
    }
  },
  methods: {
    // Changes page of posts.
    async changePage(pageNumber) {
      // Subtract from page number because programmers love to start at 0
      this.$emit("changePage", pageNumber - 1);
    },
  },
};
</script>

<style>
#post-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin: 15px auto 20px auto;
  min-width: 250px;
  width: fit-content;
}

#no-posts {
  margin-top: 40px;
  color: var(--grey);
}

@media screen and (max-width: 900px) {
  #post-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
