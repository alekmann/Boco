<template>
  <div id="org-card" @click="clickCard">
    <img
      id="org-card-background-image"
      alt="Organization image"
      v-bind:src="profilePicture"
    />
    <div id="org-info">
      <div id="name">
        <font-awesome-icon :icon="'building'" class="icon" fixed-width />
        {{ org.nickname }}
      </div>
    </div>
    <div id="org-card-image">
      <img
        class="pictures"
        alt="Organization image"
        v-bind:src="profilePicture"
      />
    </div>
  </div>
</template>

<script>
export default {
  props: {
    org: {},
  },
  data() {
    return {};
  },
  methods: {
    clickCard() {
      this.$router.push("/orgProfile/" + this.org.orgNum);
    },
  },
  computed: {
    //Loads profile picture. If none, a standard profile picture is loaded.
    profilePicture() {
      if (this.org.profilePicture) {
        return this.org.profilePicture;
      } else {
        return require("@/assets/images/orgIcon.png");
      }
    },
  },
};
</script>

<style>
#org-card {
  height: 250px;
  width: 500px;
  border-radius: var(--standard-border-radius);
  cursor: pointer;
  position: relative;
  text-align: center;
  color: white;
  object-fit: fill;
  border: solid 2px var(--grey);
}

#org-card-background-image {
  filter: blur(6px) brightness(90%);
  width: 100%;
  height: 100%;
}

.pictures {
  width: 140px;
  height: 140px;
  border: solid 2px var(--grey);
}

#org-card:hover {
  transform: scale(1.01);
}

#org-card-image {
  position: absolute;
  top: 60%;
  left: 50%;
  transform: translate(-50%, -50%);
}

#org-card-image-icon {
}

#org-info {
  padding: 12px;
  box-shadow: 2px 2px 2px 0 rgba(197, 197, 197, 0.3);
  border-radius: var(--standard-border-radius);
  background-color: #fff;
  color: var(--light-base);
  text-align: center;
  font-size: 18px;
  display: flex;
  margin: 12px;
  position: absolute;
  top: 0;
  left: 0;
}
</style>
