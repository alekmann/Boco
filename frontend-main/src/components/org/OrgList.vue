<template>
  <div id="org-list-position">
    <div id="org-list">
      <div v-for="org in organizations" :key="org">
        <OrgCard :org="org" />
      </div>
    </div>
  </div>
</template>

<script>
import OrgCard from "./OrgCard.vue";

export default {
  components: {
    OrgCard,
  },
  props: {
    organizations: Array,
  },
};
</script>

<style>
#org-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 30px;
  justify-self: center;
}

#org-list-position {
  display: flex;
  justify-content: center;
}
</style>
