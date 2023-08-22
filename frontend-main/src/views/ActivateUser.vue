<template>
  <div id="activate-user-grid">
    <div id="activate-user-container">
      <div id="active-user-button" @click="handleClick">Aktiver bruker</div>
    </div>
  </div>
</template>

<script>
import { activateUser } from "@/service/AuthenticationService";
export default {
  data() {
    return {};
  },
  methods: {
    // Activates user and pushes login site.
    async handleClick() {
      try {
        this.$store.dispatch(
          "showLoading",
          "Aktivering vellykket, omdirigerer til påloggingsside..."
        );
        setTimeout(() => {
          activateUser(this.$route.params.id);
          this.$notify({
            type: "success",
            title: "Aktivering vellykket",
            text: "Omdirigerer til påloggingsside",
          });
          this.$router.push({ name: "SignIn" });
        }, 3000);
      } catch (error) {
        this.$store.dispatch("hideLoading");
        console.log(error);
      }
    },
  },
};
</script>

<style>
#activate-user-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: 1fr 1fr 1fr;
  min-height: 80vh;
}

#activate-user-container {
  grid-column: 2/3;
  grid-row: 2/3;
  align-self: center;
  justify-self: center;
}

#active-user-button {
  border-radius: var(--standard-border-radius);
  background-color: var(--base);
  padding: 20px;
  color: var(--white);
  cursor: pointer;
  font-size: 20px;
  width: 300px;
}

#active-user-button:hover {
  background-color: var(--chill-blue);
  color: var(--color-1);
}

#activate-user-message {
  grid-column: 2/3;
  grid-row: 2/3;
  align-self: center;
  justify-self: center;
}
</style>
