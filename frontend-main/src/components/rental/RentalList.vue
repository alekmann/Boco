<template>
  <div id="rental-list-position">
    <div v-for="n in pages.length" :key="n" div>
      <div v-if="this.currentPage == n" id="rental-list">
        <div v-for="rental in this.pages[n - 1].rentals" :key="rental.rentalId">
          <RentalCard :rental="rental" />
        </div>
      </div>
    </div>
  </div>

  <!-- change page -->
  <div id="change-rental-page">
    <div id="change-rental-page-middle">
      <div id="left-arrow">
        <div id="click">
          <font-awesome-icon
            v-if="this.currentPage !== 1"
            icon="angle-left"
            @click="changePage(false)"
          />
        </div>
      </div>
      <div id="page-number">Side {{ this.currentPage }}</div>
      <div id="right-arrow">
        <div id="click">
          <font-awesome-icon
            v-if="this.currentPage !== this.pages.length"
            icon="angle-right"
            @click="changePage(true)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RentalCard from "@/components/rental/RentalCard.vue";

export default {
  props: ["rentals"],
  data() {
    return {
      currentPage: 1,
      cardsPerPage: 6,
      pages: [],
    };
  },
  components: { RentalCard },
  methods: {
    // Changes paginated page.
    changePage(up) {
      if (up) this.currentPage++;
      if (!up) this.currentPage--;
    },
  },
  mounted() {
    let page = {
      pageNumber: 1,
      rentals: [],
    };
    // Logic for paginated pages.
    for (let i = 0; i < this.rentals.length; i++) {
      page.rentals.push(this.rentals[i]);
      if (page.rentals.length >= this.cardsPerPage) {
        this.pages.push(page);
        page = {
          pageNumber: page.pageNumber + 1,
          rentals: [],
        };
      }
      if (i + 1 == this.rentals.length) {
        if (page.rentals.length != 0) this.pages.push(page);
      }
    }
  },
};
</script>

<style>
#rental-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 30px;
}

#rental-list-position {
  display: flex;
  justify-content: center;
}

#change-rental-page {
  width: 100%;
  padding: 30px 0;
  justify-content: center;
  display: grid;
  bottom: 20px;
}
#change-rental-page-middle {
  display: flex;
  justify-content: center;
  background-color: white;
  padding: 5px 0;
  border-radius: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.15);
}

#left-arrow,
#right-arrow {
  font-size: 18px;
  width: 40px;
}

#left-arrow:hover,
#right-arrow:hover {
  color: var(--chill-blue);
}

#click {
  cursor: pointer;
}

#page-number {
  align-self: center;
  font-size: 18px;
}
</style>
