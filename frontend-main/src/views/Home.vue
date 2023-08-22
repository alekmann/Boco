<template>
  <div id="home">
    <div id="searchbar">
      <div id="category-display" v-if="search.category">
        <div id="reset-category" @click="resetCategory">
          <font-awesome-icon icon="angle-left" />
        </div>
        <h2 style="margin: 0">{{ search.category.name }}</h2>
        <font-awesome-icon
          id="category-display-icon"
          :icon="search.category.icon"
        />
      </div>
      <div id="sb-container">
        <div id="bar">
          <font-awesome-icon id="mag-glass" icon="magnifying-glass" />
          <input
            @keyup.enter="test"
            type="text"
            id="search"
            placeholder="Søk"
            v-model="this.search.title"
          />
        </div>
        <div id="bar-inputs">
          <a
            id="bar-as"
            class="link"
            @click="$refs['filter-panel'].displayed = true"
            >Avansert søk</a
          >
          <a
            v-show="activeFilters"
            id="bar-reset"
            class="link"
            @click="resetSearch"
            >Nullstill</a
          >
        </div>
      </div>
    </div>
    <FilterPanel
      ref="filter-panel"
      @advancedSearch="advancedSearch"
      @resetSearch="resetSearch"
    />
    <CategoryFilter
      v-if="!search.category"
      @categorySelected="searchCategory"
    />
    <PostList v-if="activeFilters" :posts="posts" @changePage="handleSearch" />
    <PostList v-else :posts="posts" @changePage="fetchPosts" />
  </div>
</template>

<script>
import PostList from "../components/post/PostList.vue";
import { getPaginatedPosts, search } from "@/service/PostService.js";
import CategoryFilter from "@/components/filter/CategoryFilter";
import FilterPanel from "@/components/filter/FilterPanel";

export default {
  name: "Home",
  components: { FilterPanel, CategoryFilter, PostList },
  data() {
    return {
      search: {
        title: null,
        category: null,
        location: null,
        dates: null,
      },
      posts: [],
      page: 0,
      amountOfPosts: 15,
    };
  },
  methods: {
    // Fetches initial posts. Currently hardcoded to fetch 15 of them
    async fetchPosts(page = 0, amount = this.amountOfPosts) {
      try {
        const response = await getPaginatedPosts(page, amount);
        this.posts = response.data;
      } catch (error) {
        console.log(error);
      }
    },
    // Retrieves search results from database
    async handleSearch(page = 0, amount = this.amountOfPosts) {
      try {
        const response = await search(page, amount, this.search);
        this.posts = response.data;
      } catch (err) {
        console.log(err);
      }
    },
    // Expands search by mutating only category and keeping the rest search filters active.
    searchCategory(category) {
      this.search.category = category;
      this.handleSearch();
    },
    // Resets only category
    resetCategory() {
      this.search.category = null;
      this.handleSearch();
    },
    // Advances search in filterpanel
    advancedSearch(searchFilters) {
      this.search.location = searchFilters.location;
      this.search.dates = searchFilters.dates;
      this.search.category = searchFilters.category
        ? searchFilters.category
        : this.search.category;
      this.handleSearch();
    },
    // Resets all search types.
    resetSearch() {
      // Reset filter panel
      this.$refs["filter-panel"].resetSearch();

      // Reset search
      this.search.title = null;
      this.search.category = null;
      this.search.location = null;
      this.search.dates = null;

      this.handleSearch();
    },
  },
  computed: {
    // Returns true if any of the search filters are set active.
    activeFilters() {
      return !Object.values(this.search).every((field) => field === null);
    },
  },
  async created() {
    await this.fetchPosts(this.page, this.amountOfPosts);
  },
};
</script>

<style>
#home {
  display: inline-flex;
  justify-items: center;
  flex-direction: column;
}

#reset-category {
  padding: 10px 15px;
  cursor: pointer;
  font-size: 20px;
}

#searchbar {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  grid-template-areas: "left center right";
  justify-content: center;
  margin: 15px 15px;
  padding: 5px;
}

#category-display {
  justify-self: right;
  grid-area: left;
  margin-right: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 20px;
}

#category-display-icon {
  font-size: 50px;
}

#sb-container {
  grid-area: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

#bar {
  display: flex;
  align-items: center;
}

#bar-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-areas: "as reset";
  padding: 0 23px;
}

#bar-as {
  grid-area: as;
  justify-self: left;
}

#bar-reset {
  grid-area: reset;
  justify-self: right;
}

#search {
  padding: 0 10px;
  border-radius: 30px;
  border: solid 1px var(--grey);
  background-color: white;
  color: var(--base);
  display: flex;
  justify-self: start;
  margin: 0 10px;
  width: 400px;
}

#search:hover,
#search:focus-within {
  border-color: var(--calm-blue);
}

#mag-glass {
  position: absolute;
  font-size: 18px;
  color: var(--grey);
  margin-left: 20px;
}
</style>
