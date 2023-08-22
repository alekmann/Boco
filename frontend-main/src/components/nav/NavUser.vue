<template>
  <div
    id="user-component"
    tabindex="0"
    @focusin="openUserPanel"
    @focusout="closeUserPanel"
  >
    <div id="user-container" @click="openIfClosed">
      <div id="highlighter" />
      <img
        id="user-icon"
        class="pictures"
        alt="user image"
        v-bind:src="profilePicture"
      />
    </div>
    <UserPanel
      v-show="displayUserPanel"
      @closePanel="closeUserPanel"
      id="user-panel"
    />
  </div>
</template>

<script>
import UserPanel from "@/components/nav/UserPanel";
import { mapState } from "vuex";

export default {
  name: "NavUser",
  components: { UserPanel },
  data() {
    return {
      displayUserPanel: false,
      handleOutsideClick: null,
      userPanelFocus: false,
    };
  },

  methods: {
    openIfClosed() {
      if (!this.displayUserPanel) {
        this.openUserPanel();
      }
    },
    openUserPanel() {
      this.displayUserPanel = true;
    },
    closeUserPanel() {
      this.displayUserPanel = false;
    },
  },
  computed: {
    ...mapState({
      selectedOrganization: "selectedOrganization",
    }),
    profilePicture() {
      if (this.selectedOrganization) {
        return this.$store.getters.getOrgPicture;
      } else {
        return this.$store.getters.getUserPicture;
      }
    },
  },
};
</script>

<style scoped>
#user-component,
#user-container {
  display: flex;
  align-items: center;
  justify-content: center;
  align-content: center;
}

#user-container {
  width: 36px;
  margin-left: 10px;
  cursor: pointer;
}

#user-icon,
#highlighter {
  height: 32px;
  width: 32px;
  border-radius: 100%;
  background-color: var(--white);
  border: solid 0 transparent;
  transition: 0.15s;
  z-index: 102;
}

#highlighter {
  position: absolute;
  height: 36px;
  width: 36px;
  background-color: var(--white);
  z-index: 101;
  opacity: 0;
}

#user-container:hover > #highlighter {
  opacity: 1;
}
</style>
