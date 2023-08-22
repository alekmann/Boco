<template>
  <div id="nav">
    <img alt="logo" @click="routeHome" src="@/assets/boco.svg" id="nav-logo" />
    <div id="nav-buttons" v-if="this.user.firstName != null">
      <NavButton
        icon="plus"
        tooltip-text="Opprett annonse"
        @click="toggleCreatePost"
      />
      <NavNotification />
      <NavUser />
    </div>
    <div v-else @click="logInClick" id="login-button">Logg inn</div>
  </div>
  <div id="cp-container" v-if="createPost">
    <div id="cp-bg" @click="toggleCreatePost" />
    <CreatePost @postCreated="postCreated" />
  </div>
</template>

<script>
import NavButton from "@/components/nav/NavButton";
import NavUser from "@/components/nav/NavUser";
import CreatePost from "@/components/post/CreatePost";
import { mapState } from "vuex";
import NavNotification from "@/components/nav/NavNotification";

export default {
  name: "Navbar",
  components: {
    NavNotification,
    CreatePost,
    NavUser,
    NavButton,
  },
  data() {
    return {
      createPost: false,
    };
  },
  computed: {
    ...mapState(["user"]),
  },
  methods: {
    routeHome() {
      this.$router.push("/");
    },
    toggleCreatePost() {
      this.createPost = !this.createPost;
    },
    logInClick() {
      this.$router.push(`/signin`);
    },
    postCreated() {
      this.toggleCreatePost();
      this.$notify({
        type: "success",
        title: "Annonse opprettet",
        text: "Annonsen ble opprettet.",
      });
    },
  },
};
</script>

<style scoped>
#nav {
  position: fixed;
  top: 0;
  background-color: var(--dark-blue);
  padding: 0 10px 0 10px;
  height: 50px;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: row;
  flex-wrap: nowrap;
  transition: top 0.2s;
  z-index: 100;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
  border-bottom: solid 1px lightgray;
}

#nav-logo {
  height: 60%;
  margin-right: auto;
  cursor: pointer;
  margin-left: 5px;
}

#nav-buttons {
  display: inline-flex;
  align-content: baseline;
  margin: 0 5px 0 auto;
  padding-right: 30px;
  gap: 10px;
  height: 100%;
}

#cp-container {
  position: fixed;
  top: 0;
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

#cp-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgb(0, 0, 0, 0.8);
  z-index: 5;
}

#login-button {
  padding: 5px 30px;
  color: var(--white);
  border-radius: var(--standard-border-radius);
  border: 1px solid var(--white);
  margin: 0 30px;
  cursor: pointer;
}
</style>
