<template>
  <h1>Database filled</h1>
</template>

<script>
import http from "../service/http.js";

export default {
  // Fills database with test objects.
  async created() {
    http.get("/fill");
  },
};
</script>
