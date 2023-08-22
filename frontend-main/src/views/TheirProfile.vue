<template>
  <div id="profile-grid">
    <div class="profileCard">
      <div class="profilePicture">
        <img
          class="pictures"
          v-bind:src="profilePicture"
          alt="profile picture"
        />
      </div>
      <div class="profileInformation">
        <ul id="profileInformationList">
          <li v-for="(profileElement, index) in profileInfo" :key="index">
            {{ profileElement }}
          </li>
        </ul>
      </div>
    </div>
    <h1 id="active-profiles">Aktive annonser</h1>
    <div id="post-list-grid-profile">
      <PostList id="post-list" :posts="posts" @changePage="fetchProfile" />
    </div>
  </div>
</template>

<script>
import PostList from "@/components/post/PostList";
import { getUserPosts, getOrganizationPosts } from "@/service/PostService";
import { getUserInfo } from "@/service/AdministrateUserService";
import { getOrganizationInfo } from "@/service/OrganizationService";

export default {
  name: "Profile.vue",
  components: { PostList },
  watch: {
    $route() {
      this.fetchProfile();
    },
  },
  data() {
    return {
      profileInfo: Array,
      baseUrl: "",
      posts: Array,
      profilePicture: null,
    };
  },
  methods: {
    // Collecting and sets up profile page. Either for person or organization.
    async fetchProfile(page = 0) {
      try {
        if (this.$route.path.includes("userProfile")) {
          const responsePaginatedPosts = await getUserPosts(
            this.$route.params.id,
            page,
            6
          );
          this.posts = responsePaginatedPosts.data;
          const response = await getUserInfo(this.$route.params.id);
          this.profileInfo = [
            "Email: " + response.data.email,
            "Navn: " + response.data.firstName + " " + response.data.lastName,
          ];
          if (response.data.profilePicture) {
            this.profilePicture = response.data.profilePicture;
          } else {
            this.profilePicture = require("@/assets/images/userIcon.jpeg");
          }
        } else {
          const responsePaginatedPosts = await getOrganizationPosts(
            this.$route.params.id,
            page,
            9
          );
          this.posts = responsePaginatedPosts.data;

          const response = await getOrganizationInfo(this.$route.params.id);
          this.profileInfo = [
            "Organisasjonsnummer: " + response.data.orgNum,
            "Organisasjonsnavn: " + response.data.orgName,
          ];
          if (response.data.profilePicture) {
            this.profilePicture = response.data.profilePicture;
          } else {
            this.profilePicture = require("@/assets/images/userIcon.jpeg");
          }
        }
      } catch (error) {
        console.log(error);
      }
    },
  },
  async created() {
    await this.fetchProfile();
  },
};
</script>

<style scoped>
hr {
  margin-top: -30px;
  margin-bottom: 20px;
  width: 102%;
  justify-self: center;
}

#profile-grid {
  display: grid;
  grid-template-columns: 100%;
  justify-content: center;
}

#post-list-grid-profile {
  justify-self: center;
  width: auto;
}

.pictures {
  width: 200px;
  height: 200px;
}

.profileInformation > #profileInformationList > li {
  list-style: none;
  text-align: start;
  padding: 10px;
}

#active-profiles {
  margin-bottom: 25px;
}
</style>
