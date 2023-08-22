<template>
  <div class="profileGrid">
    <div class="sidebar">
      <div class="profile-sidebar-button" @click="pageScroll('aktive')">
        Aktive
      </div>
      <div class="profile-sidebar-button" @click="pageScroll('ventende')">
        Ventende
      </div>
      <div class="profile-sidebar-button" @click="pageScroll('historikk')">
        Historikk
      </div>
    </div>
    <div class="main">
      <div ref="aktive">
        <h1>Aktive</h1>
        <RentalList
          :rentals="this.rentalsApproved"
          v-if="this.rentalsApproved.length > 0"
        />
        <div v-if="this.rentalsApprovedCollecting == true">
          Henter leiesøknader...
        </div>
        <div
          v-if="
            this.rentalsApproved.length == 0 &&
            this.rentalsApprovedCollecting == false
          "
        >
          Ingen leiesøknader
        </div>
      </div>
      <div ref="ventende">
        <h1>Ventende</h1>
        <RentalList
          :rentals="this.rentalsNotApproved"
          v-if="this.rentalsNotApproved.length > 0"
        />
      </div>

      <div v-if="this.rentalsNotApprovedCollecting == true">
        Henter leiesøknader...
      </div>
      <div
        v-if="
          this.rentalsNotApproved.length == 0 &&
          this.rentalsNotApprovedCollecting == false
        "
      >
        Ingen leiesøknader
      </div>

      <div ref="historikk">
        <h1>Historikk</h1>
        <RentalList
          :rentals="this.rentalsHistory"
          v-if="this.rentalsHistory.length > 0"
        />
      </div>
      <div v-if="this.rentalsHistoryCollecting == true">
        Henter leiesøknader...
      </div>
      <div
        v-if="
          rentalsHistory.length == 0 && this.rentalsHistoryCollecting == false
        "
      >
        Ingen leiesøknader
      </div>
    </div>
  </div>
</template>

<script>
import RentalList from "@/components/rental/RentalList.vue";
import { getRentals } from "@/service/RentalService.js";

export default {
  name: "Applications",
  components: { RentalList },
  data() {
    return {
      rentalsApproved: [],
      rentalsApprovedCollecting: true,
      rentalsNotApproved: [],
      rentalsNotApprovedCollecting: true,
      rentalsHistory: [],
      rentalsHistoryCollecting: true,
    };
  },
  methods: {
    pageScroll(pageElement) {
      var element = this.$refs[pageElement];
      var top = element.offsetTop - 50;

      window.scrollTo(0, top);
    },
  },
  async created() {
    const org = this.$store.getters.getOrganization;
    const response = await getRentals(org);

    let rentals = response.data;
    if (org) {
      rentals = rentals.filter(
        (rental) => rental.organizationCustomer?.orgNum === org.orgNum
      );
    } else {
      rentals = rentals.filter(
        (rental) => rental.organizationCustomer === null
      );
    }

    // date now
    var newdate = new Date();

    // add to lists
    for (let i = 0; i < rentals.length; i++) {
      // date of rental
      var rentalDate = new Date(rentals[i].toDate);

      // check if rental toDate is less than date now
      if (rentalDate < newdate) {
        this.rentalsHistory.push(rentals[i]);
      } else {
        if (rentals[i].approved) this.rentalsApproved.push(rentals[i]);
        if (!rentals[i].approved) this.rentalsNotApproved.push(rentals[i]);
      }
    }
    this.rentalsApprovedCollecting = false;
    this.rentalsNotApprovedCollecting = false;
    this.rentalsHistoryCollecting = false;
  },
};
</script>

<style scoped></style>
