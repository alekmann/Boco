<template>
  <div class="profileNavigation">
    <div id="profile-navigation-placement">
      <router-link class="profileNavigationElements" to="/myProfile"
        >Mine annonser</router-link
      >
      <router-link
        class="profileNavigationElements"
        to="/myProfile/applications"
        >Mine søknader</router-link
      >
      <router-link
        class="profileNavigationElements"
        to="/myProfile/theirApplications"
        >Mine utleieforhold</router-link
      >
      <router-link
        class="profileNavigationElements"
        to="/myProfile/administrateuser"
        >Administrer konto</router-link
      >
      <router-link
        class="profileNavigationElements"
        to="/myProfile/organization"
        >Organisasjon</router-link
      >
    </div>
  </div>
</template>

<script>
export default {
  name: "userprofileNav",
};
</script>

<style scoped></style>
