<template>
  <div id="cf-container">
    <div
      class="cf-category"
      v-for="(category, index) in categories"
      :key="index"
      @click="selectCategory(category)"
    >
      <font-awesome-icon class="cf-icon" :icon="category.icon" />
      <p>{{ category.name }}</p>
    </div>
  </div>
</template>

<script>
import { getAllCategories } from "@/service/CategoryService";
export default {
  name: "CategoryFilter",
  data() {
    return {
      categories: [],
    };
  },
  methods: {
    selectCategory(category) {
      this.$emit("categorySelected", category);
    },
  },
  async created() {
    // Retrieve categories
    try {
      const response = await getAllCategories();
      this.categories = response.data;
    } catch (error) {
      console.log(error);
    }
  },
};
</script>
<style scoped>
#cf-container {
  min-width: 100%;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  margin-bottom: 15px;
}

.cf-category {
  display: flex;
  justify-items: center;
  flex-direction: column;
  margin-bottom: 20px;
  cursor: pointer;
  width: fit-content;
  justify-self: center;
}

.cf-category > p {
  margin: 0;
}

.cf-icon {
  font-size: 40px;
  justify-self: center;
  align-self: center;
  padding: 10px;
  border-radius: var(--standard-border-radius);
}

.cf-category > p,
.cf-icon {
  color: var(--chill-blue);
}

.cf-category:hover > .cf-icon {
  background-color: var(--light-grey);
}
</style>
