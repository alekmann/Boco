<template>
  <div class="profileNavigation">
    <div id="profile-navigation-placement">
      <router-link class="profileNavigationElements" to="/myOrganization"
        >Bedriftens annonser</router-link
      >
      <router-link
        class="profileNavigationElements"
        to="/myOrganization/applications"
        >Bedriftens søknader</router-link
      >
      <router-link
        class="profileNavigationElements"
        to="/myOrganization/theirApplications"
        >Bedriftens utleieforhold</router-link
      >
      <router-link
        class="profileNavigationElements"
        to="/myOrganization/administrateorg"
        >Administrer</router-link
      >
      <router-link
        class="profileNavigationElements"
        to="/myOrganization/members"
        >Medlemmer</router-link
      >
    </div>
  </div>
</template>

<script>
export default {
  name: "orgprofileNav",
};
</script>

<style scoped></style>
