<template>
  <div id="filter-panel-container">
    <div ref="filter-panel" id="filter-panel">
      <div id="fp-container">
        <div id="fp-content">
          <div id="fp-header">
            <font-awesome-icon
              id="close-button"
              icon="xmark"
              @click="toggleDisplayed"
            />
            <h2 style="margin: 0">Avansert søk</h2>
          </div>
          <Form ref="form" @submit="onSubmit" v-slot="{ meta }">
            <label class="fp-label">Sted</label>
            <div id="fp-map">
              <MapRadius @changeMapLocation="updateMapLocation" ref="map" />
            </div>
            <!-- Categories !-->
            <div class="input-container" id="fp-category">
              <label class="fp-label">Kategori</label>
              <select
                ref="fp-category"
                name="category"
                v-model="filterSettings.category"
                class="input select"
              >
                <option
                  v-for="category in categories"
                  :key="category"
                  :value="category"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>
            <!-- Dates !-->
            <div class="input-container" id="fp-date">
              <label class="fp-label">Periode</label>
              <DateFilter @datesSelected="updateDateRange" ref="date-filter" />
            </div>
            <div id="fp-buttons">
              <button
                id="fp-search"
                class="fp-button"
                :disabled="!meta.valid"
                ref="submit-button"
              >
                Søk
              </button>
              <a id="fp-empty" class="link" @click="$emit('resetSearch')"
                >Nullstill</a
              >
            </div>
          </Form>
        </div>
      </div>
      <div id="flap" @click="toggleDisplayed" v-show="!displayed">
        <font-awesome-icon v-if="displayed" class="fp-icon" icon="angle-left" />
        <font-awesome-icon v-else class="fp-icon" icon="angle-right" />
        <img id="flap-img" src="@/assets/flap.svg" alt="flap" />
      </div>
    </div>
  </div>
</template>

<script>
import { Form } from "vee-validate";
import { getAllCategories } from "@/service/CategoryService";
import MapRadius from "@/components/map/MapRadius.vue";
import DateFilter from "@/components/filter/DateFilter";

export default {
  components: { MapRadius, Form, DateFilter },
  name: "FilterPanel",
  data() {
    return {
      categories: {},
      displayed: false,
      filterSettings: {
        location: null,
        category: null,
        dates: null,
      },
    };
  },
  watch: {
    displayed() {
      if (!this.displayed) {
        this.$refs["filter-panel"].style.marginLeft = "-340px";
      } else {
        this.$refs["filter-panel"].style.marginLeft = "0px";
      }
    },
  },
  methods: {
    toggleDisplayed() {
      this.displayed = !this.displayed;
    },
    async onSubmit() {
      // Emit event to parent
      this.$emit("advancedSearch", this.filterSettings);
    },
    triggerSubmit() {
      this.$refs["submit-button"].click();
    },
    updateMapLocation(location) {
      this.filterSettings.location = location;
    },
    updateDateRange(range) {
      this.filterSettings.dates = range;
    },
    resetSearch() {
      // Refresh map
      this.$refs.map.resetMap();
      this.filterSettings.location = null;

      // Reset category
      this.filterSettings.category = null;

      // Reset dates
      this.$refs["date-filter"].reset();

      // Reset form
      this.$refs.form.resetForm();
    },
  },
  async created() {
    // Retrieve categories from database upon component load.
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
.fp-label {
  margin-bottom: 3px;
}

.fp-icon {
  position: absolute;
  font-size: 25px;
  margin-left: 10px;
}

#filter-panel-container {
  position: fixed;
  z-index: 50;
  left: 0;
  padding-right: 10px;
  margin-top: -40px;
}

#filter-panel {
  display: grid;
  grid-template-columns: auto auto;
  filter: drop-shadow(0 0 4px rgb(0 0 0 / 0.2));
  transition: 150ms;
  margin-left: -340px;
}

#fp-container {
  height: 100vh;
  background-color: white;
  min-width: 300px;
  display: grid;
  grid-template-columns: 1fr auto;
  grid-template-areas: "fill content";
}

#fp-content > Form {
  justify-content: left;
  row-gap: normal;
  text-align: left;
}

#fp-content {
  grid-area: content;
  padding: 45px 20px 80px 20px;
  text-align: left;
  overflow-y: auto;
}

#fp-header {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  grid-template-areas: "left center right";
  text-align: center;
  margin-bottom: 5px;
}

#fp-header > h2 {
  grid-area: center;
}

#close-button {
  grid-area: right;
  font-size: 20px;
  color: var(--grey);
  cursor: pointer;
  align-self: center;
  justify-self: right;
}

#fp-category {
  margin: 3px 0 10px 0;
}

#fp-category > select {
  margin-top: 3px;
}

#fp-map {
  overflow: hidden;
  border-radius: var(--standard-border-radius);
}

#fp-date {
  margin-bottom: 10px;
}

#fp-buttons {
  margin-top: 20px;
  display: grid;
  grid-template-rows: auto 1fr;
  row-gap: 10px;
}

#fp-search {
  justify-self: right;
  width: 100%;
}

#fp-empty {
  align-self: flex-end;
  justify-self: center;
}

#flap {
  display: flex;
  align-items: center;
  right: -30px;
  align-self: center;
  cursor: pointer;
}

#flap-img {
  width: 30px;
  fill: var(--dark-blue);
}
</style>
