<template>
  <notifications position="bottom right" classes="notifications" />
  <!--  Loading spinner  -->
  <vue-element-loading
    :active="this.$store.state.loadSpinner"
    spinner="spinner"
    color="#FF6700"
    size="115"
    :text="this.$store.state.loadingMessage"
    is-full-screen
  />

  <div v-if="!isLoading">
    <Navbar />
    <router-view />
  </div>
  <footer>
    <div id="footer-left">
      <img id="footer-logo" src="@/assets/boco.svg" alt="Boco" />
    </div>
    <div id="footer-center">
      <p>© Boco 2022</p>
      <p>IDATT2106 Gruppe 10</p>
      <a class="link" @click="this.$router.push('/faq')">
        Ofte stilte spørsmål</a
      >
    </div>
  </footer>
</template>

<script>
import Navbar from "@/components/nav/Navbar";

export default {
  components: { Navbar },
  data() {
    return {
      isLoading: false,
    };
  },
  async beforeMount() {
    this.isLoading = true;
    // Authentication
    try {
      await this.$store.dispatch("fetchAccessToken");
      await this.$store.dispatch("fetchUser");
    } catch (error) {
      // Ignore
    } finally {
      this.isLoading = false;
    }
  },
};
</script>
<style>
@import "assets/styles/global.css";

#app {
  display: flex;
  flex-direction: column;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  padding-top: 60px;
  min-height: calc(100vh - 60px);
  min-width: 600px;
}

/* Footer */
footer {
  margin-top: auto;
  display: grid;
  grid-template-areas: "left center right";
  grid-auto-columns: 1fr auto 1fr;
  align-items: center;
  height: 120px;
  border-top: solid 1px var(--grey);
  background-color: var(--light-grey);
  color: var(--light-base);
  width: 100%;
  gap: 20px;
}

#footer-left {
  grid-area: left;
  display: flex;
  justify-content: flex-end;
}

#footer-logo {
  height: 72px;
}

#footer-center {
  grid-area: center;
  display: inline-flex;
  flex-direction: column;
  flex-wrap: nowrap;
}

#footer-center > p {
  margin: 0;
}
</style>

<style lang="scss">
.vue-notification-group {
  margin-bottom: 5vh;
  margin-right: 5vw;
}

.notifications {
  border-radius: 10px;
  padding: 10px 20px;
  background-color: var(--dark-blue);
  margin-bottom: 5px;
  box-shadow: var(--standard-box-shadow);

  .notification-title {
    color: var(--green);
    white-space: nowrap;
  }

  .notification-content {
    color: white;
  }

  &.success {
    .notification-title {
      color: var(--green);
    }
  }
  &.info {
    .notification-title {
      color: var(--orange);
    }
  }
  &.error {
    .notification-title {
      color: var(--calm-red);
    }
  }
}
</style>
