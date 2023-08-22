<template>
  <div id="profile-grid">
    <div class="profileCard">
      <div class="profilePicture">
        <img
          class="pictures"
          data-testid="profile-picture"
          v-bind:src="getUserPicture"
          alt="picture"
        />
      </div>
      <div class="profileInformation">
        <p>{{ "Navn: " + this.user.firstName + " " + this.user.lastName }}</p>
        <p>{{ "Email: " + this.user.email }}</p>
      </div>
    </div>

    <userprofileNav />

    <router-view />
  </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import userprofileNav from "@/components/profile/nav/userprofileNav";

export default {
  name: "MyProfile",
  components: {
    userprofileNav,
  },
  data() {
    return {
      user: this.$store.getters.getUser,
    };
  },
  methods: {},
  computed: {
    ...mapState({
      selectedOrganization: "selectedOrganization",
    }),
    ...mapGetters({
      getUserPicture: "getUserPicture",
    }),
  },
  beforeMount() {
    // If org is on user page, move them to org page
    if (this.selectedOrganization) {
      this.$router.push("/myOrganization");
    }
  },
};
</script>

<style scoped>
#profile-grid {
  display: grid;
  grid-template-rows: auto auto auto;
}

.profilePicture > img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 50%;
}

.profileCard {
  grid-row: 1/2;
  padding: 50px 0px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pictures {
  width: 200px;
  height: 200px;
}

.profileInformation {
  list-style: none;
  text-align: start;
  padding: 10px;
}
</style>
