<template>
  <div class="profileGrid">
    <div class="sidebar">
      <div class="profile-sidebar-button" @click="pageScroll('aktive')">
        Aktive
      </div>
    </div>

    <div class="main">
      <div ref="aktive">
        <h1 id="active-header">Aktive</h1>
        <PostList :posts="posts" @changePage="fetchPosts" />
      </div>
    </div>
  </div>
</template>

<script>
import PostList from "@/components/post/PostList.vue";
import { getUserPosts, getOrganizationPosts } from "@/service/PostService.js";
import { mapState } from "vuex";

export default {
  name: "Posts",
  components: { PostList },
  data() {
    return {
      posts: "",
      baseURL: "",
      page: 0,
      amount: 6,
    };
  },
  methods: {
    async fetchPosts(page = 0) {
      // Collects active posts from the logged-in user.
      const user = this.$store.getters.getUser;
      const organization = this.selectedOrganization;
      try {
        let response;
        if (organization) {
          response = await getOrganizationPosts(organization.orgNum, page, 9);
        } else {
          response = await getUserPosts(user.id, page, 9);
        }
        this.posts = response.data;
      } catch (error) {
        console.log(error);
      }
    },
    pageScroll(pageElement) {
      var element = this.$refs[pageElement];
      var top = element.offsetTop - 50;

      window.scrollTo(0, top);
    },
  },
  computed: {
    ...mapState(["user", "selectedOrganization"]),
  },
  created() {
    this.fetchPosts();
  },
};
</script>

<style scoped>
#active {
  display: flex;
  justify-items: center;
  flex-direction: column;
}
post-list {
  justify-items: center;
  justify-self: center;
  justify-content: center;
}
</style>
