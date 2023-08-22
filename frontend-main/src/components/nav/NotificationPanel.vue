<template>
  <div ref="np" id="notification-panel">
    <h3 v-if="requests.length === 0" id="no-notifications">
      Du har ingen varsler
    </h3>
    <div v-else v-for="request in requests" :key="request.rentalId">
      <RequestNotification :request="request" @requestAnswered="handleAnswer" />
      <hr />
    </div>
  </div>
</template>

<script>
import RequestNotification from "@/components/nav/RequestNotification";
import {
  getRentalRequests,
  getRentalRequestsCount,
} from "@/service/RentalService";

export default {
  name: "NotificationPanel",
  components: { RequestNotification },
  data() {
    return {
      requests: [],
    };
  },
  methods: {
    handleAnswer(id) {
      this.requests = this.requests.filter(
        (request) => request.rentalId !== id
      );
      this.$emit("requestAnswered");
      // this.fetchRentalRequests();
    },
    async fetchRentalRequests() {
      try {
        const response = await getRentalRequests(false);
        this.requests = response.data;
      } catch (err) {
        console.log(err);
      }
    },
    async fetchRentalRequestsCount() {
      try {
        const response = await getRentalRequestsCount();

        // new requests ?
        // compare requests count with old count (it's saved in the store)
        if (response.data !== this.$store.state.rentalRequestsCount) {
          this.$store.state.rentalRequestsCount = response.data;
          await this.fetchRentalRequests();
          this.$emit("newNotifications", this.requests.length);
          this.$notify({
            type: "success",
            title: "Nye varslinger",
            text: `Du har ${this.$store.state.rentalRequestsCount} leieforespørsler`,
            duration: 3000,
          });
        }
      } catch (err) {
        console.log(err);
      }
    },
  },
  created() {
    this.fetchRentalRequestsCount();
    this.$watch(
      () => this.$route.params,
      () => {
        this.fetchRentalRequestsCount();
      }
    );
  },
};
</script>

<style scoped>
hr {
  background-color: var(--chill-blue);
}

#notification-panel {
  overflow: auto;
  max-height: 80vh;
  position: absolute;
  background-color: var(--dark-blue);
  color: var(--white);
  top: 0;
  right: 20px;
  margin: 70px 20px 0 0;
  border-radius: 10px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
  z-index: 80;
  display: flex;
  justify-items: center;
  flex-direction: column;
  gap: 5px;
  padding: 10px 0 10px 0;
  min-width: 23em;
}

#no-notifications {
  margin: 100px 40px 100px 40px;
  color: var(--grey);
}
</style>
