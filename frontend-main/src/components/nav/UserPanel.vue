<template>
  <div id="user-panel" ref="userpanel">
    <section id="current-user">
      <img class="pictures" alt="user image" v-bind:src="profilePicture" />
      <div id="up-info">
        <div v-if="this.selectedOrganization == null">
          <p class="up-name">
            {{ this.user.firstName + " " + this.user.lastName }}
          </p>
        </div>
        <div v-else>
          <p class="up-name">
            {{ this.selectedOrganization.orgName }}
          </p>
        </div>
        <a
          class="link"
          id="up-my-page"
          @click="myProfile"
          v-if="this.selectedOrganization === null"
          >Min side</a
        >
        <a class="link" id="up-my-page" @click="myOrganization" v-else
          >Organisasjonens side</a
        >
      </div>
    </section>
    <hr />
    <section id="available-users">
      <UserSelector
        :name="this.user.firstName + ' ' + this.user.lastName"
        :selected="selectedOrganization === null"
        :picture="this.getUserPicture"
        @click="selectOrg(null)"
      />
      <UserSelector
        v-for="organization in this.user.organizations"
        :key="organization.orgNum"
        :name="organization.orgName"
        :selected="representingOrg(organization)"
        :picture="this.getOrgPictureSearch(organization)"
        @click="selectOrg(organization)"
      />
    </section>
    <hr style="margin-top: 1px" />
    <section id="up-logout">
      <p id="up-logout-text" @click="logOutClick">Logg ut</p>
    </section>
  </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import UserSelector from "@/components/nav/UserSelector";
import { logout } from "@/service/AuthenticationService.js";

export default {
  name: "UserPanel",
  components: { UserSelector },
  data() {
    return {
      renderComponent: true,
    };
  },
  computed: {
    ...mapState(["user", "selectedOrganization"]),
    ...mapGetters(["getOrgPictureSearch", "getUserPicture"]),
    profilePicture() {
      if (this.selectedOrganization) {
        return this.$store.getters.getOrgPicture;
      } else {
        return this.$store.getters.getUserPicture;
      }
    },
  },
  methods: {
    myProfile() {
      this.$router.push(`/myProfile`);
      this.$emit("closePanel");
    },
    myOrganization() {
      this.$router.push(`/myOrganization`);
      this.$emit("closePanel");
    },
    selectOrg(organization) {
      this.$store.dispatch("setSelectedOrganization", organization);
      if (this.$route.path === "/myOrganization" && !organization) {
        this.$router.push(`/myProfile`);
      } else if (this.$route.path === "/myProfile" && organization) {
        this.$router.push(`/myOrganization`);
      }
      this.$emit("closePanel");

      // Notification
      if (organization === null) {
        this.$notify({
          type: "success",
          title: "Privat bruker",
          text: "Du opererer nå som deg selv.",
        });
      } else {
        this.$notify({
          type: "info",
          title: "Organisasjon",
          text: "Du opererer nå på vegne av " + organization.orgName + ".",
        });
      }
    },
    logOutClick() {
      logout();
      this.$store.dispatch("saveAccessToken", undefined);
      this.$store.dispatch("setSelectedOrganization", null);
      this.$store.dispatch("setUser", {
        firstname: null,
        lastname: null,
        image: null,
      });
      this.$router.push(`/signin`);
      this.forceRerender();
      this.$emit("closePanel");
      this.$notify({
        type: "success",
        title: "Logget ut",
        text: "Du er nå logget ut av brukeren din.",
      });
    },
    forceRerender() {
      // Removing my-component from the DOM
      this.renderComponent = false;

      this.$nextTick(() => {
        // Adding the component back in
        this.renderComponent = true;
      });
    },
    representingOrg(extOrg) {
      if (extOrg === null || this.selectedOrganization === null) {
        return false;
      }
      return extOrg.orgNum === this.selectedOrganization.orgNum;
    },
  },
};
</script>

<style scoped>
hr {
  background-color: var(--chill-blue);
}

.up-name {
  margin: 0 20px 0 0;
  padding-right: 20px;
  font-weight: bold;
  color: white;
  white-space: nowrap;
}

.pictures {
  height: 40px;
  width: 40px;
}

#user-panel {
  position: absolute;
  display: flex;
  justify-items: center;
  flex-direction: column;
  background-color: var(--dark-blue);
  color: var(--white);
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
  top: 0;
  right: 20px;
  margin: 50px 0 0 0;
  cursor: default;
  border-bottom-left-radius: 10px;
  border-left: solid 1px var(--chill-blue);
  border-bottom: solid 1px var(--chill-blue);
  border-top: solid 1px var(--chill-blue);
  z-index: 101;
  width: auto;
  padding: 10px 0 0 0;
}

#current-user {
  display: flex;
  justify-items: center;
  flex-direction: row;
  gap: 10px;
  margin: 10px 2vw 11px 2vw;
  width: auto;
}

#up-info {
  display: flex;
  justify-content: flex-end;
  align-items: start;
  flex-direction: column;
  flex-wrap: nowrap;
  float: right;
}

#up-my-page {
  text-decoration: none;
  color: var(--calm-blue);
}

#up-my-page:hover {
  color: var(--light-blue);
}

#available-users {
  display: flex;
  flex-direction: column;
  padding-top: 3px;
}

#available-users > div:last-of-type {
  margin-bottom: -2px;
}

#up-logout {
  display: flex;
  justify-content: center;
  align-content: center;
  flex-wrap: nowrap;
  border-bottom-left-radius: 10px;
  cursor: pointer;
  margin-top: -1px;
}

#up-logout:hover {
  background-color: var(--chill-blue);
}

#up-logout-text {
  margin: 10px 0 10px 0;
  color: var(--red);
}

#up-login-text {
  color: var(--white);
  padding: 0 50px;
}
</style>
