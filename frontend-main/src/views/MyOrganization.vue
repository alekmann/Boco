<template>
  <div id="profile-grid">
    <div class="profileCard">
      <div class="profilePicture">
        <img
          class="pictures"
          data-testid="profile-picture"
          v-bind:src="this.getOrgPicture"
          alt="picture"
        />
      </div>
      <div class="profileInformation">
        <p>{{ this.profileName }}</p>
        <p>{{ this.profileNumber }}</p>
      </div>
    </div>
    <orgprofileNav />
    <router-view />
  </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import orgprofileNav from "@/components/profile/nav/orgprofileNav";

export default {
  name: "MyOrganization",
  components: {
    orgprofileNav,
  },
  data() {
    return {};
  },
  methods: {},
  computed: {
    ...mapState({
      selectedOrganization: "selectedOrganization",
    }),
    ...mapGetters({
      getOrgPicture: "getOrgPicture",
      getOrganization: "getOrganization",
    }),
    profileName() {
      let name = "Organisasjonsnavn: ";
      if (this.selectedOrganization) {
        name += this.selectedOrganization.orgName;
      }
      return name;
    },
    profileNumber() {
      let name = "Organisasjonsnummer: ";
      if (this.selectedOrganization) {
        name += this.selectedOrganization.orgNum;
      }
      return name;
    },
  },
  beforeMount() {
    // If user is on org page, move them to user page
    if (!this.selectedOrganization) {
      this.$router.push("/myProfile");
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
