<template>
  <div @click="pushToPost" id="rental-card">
    <img id="rental-card-image" v-bind:src="this.image" />
    <div id="rental-card-content">
      <div class="rental-card-row rental-card-top">
        <div id="rental-card-title">{{ rental.post.title }}</div>
        <div>{{ rental.totalPrice }} kr</div>
      </div>
      <div class="rental-card-row" v-if="rental.post.organization == null">
        <div @click.stop="routeToUserProfile" class="rental-card-renter">
          {{
            rental.organizationCustomer
              ? rental.organizationCustomer.orgName
              : rental.owner.firstName + " " + rental.owner.lastName
          }}
        </div>
      </div>
      <div class="rental-card-row" v-else>
        <div @click="routeToOrgProfile" class="rental-card-renter">
          {{ rental.post.organization.orgName }}
        </div>
      </div>
      <div class="rental-card-row">
        <div id="rental-card-date">
          Leieperiode:
          {{ formatDate(rental.fromDate) + " - " + formatDate(rental.toDate) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ["rental"],
  data() {
    return {
      image: "",
    };
  },
  methods: {
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString("no", {
        year: "2-digit",
        month: "2-digit",
        day: "2-digit",
      });
    },

    routeToUserProfile() {
      this.$router.push("/userProfile/" + this.rental.owner.id);
    },

    routeToOrgProfile() {
      this.$router.push("/orgProfile/" + this.rental.post.organization.orgNum);
    },
    pushToPost() {
      this.$router.push("/posts/" + this.rental.post.postId);
    },
  },
  created() {
    // Sets picture, replaces tag marks.
    if (this.rental.post.pictures) {
      this.image = this.rental.post.pictures[0].file
        .replace('"', "")
        .replace('"', "");
    }
  },
};
</script>

<style>
#rental-card {
  display: grid;
  box-shadow: 0 2px 10px 0 rgba(0, 0, 0, 0.3);
  grid-template-columns: 100px auto;
  grid-template-rows: auto auto;
  transition: all 0.15s;
  border-radius: var(--standard-border-radius);
  min-width: 400px;
  background-color: var(--white);
  cursor: pointer;
}

#rental-card-image {
  grid-column: 1/2;
  position: relative;
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-top-left-radius: var(--standard-border-radius);
  border-bottom-left-radius: var(--standard-border-radius);
}

#rental-card:hover {
  transform: scale(1.01);
}

#rental-card-content {
  grid-column: 2/3;
  display: grid;
  padding: 10px;
}

.rental-card-row {
  justify-self: start;
}

.rental-card-top {
  display: flex;
  justify-content: space-between;
  width: 90%;
  font-weight: bold;
  font-size: 14px;
}

#rental-card-title {
  font-size: 16px;
  font-weight: bold;
}

.rental-card-renter {
  font-size: 14px;
  color: var(--calm-blue);
}

.rental-card-renter:hover {
  cursor: pointer;
  text-decoration: underline;
}

#rental-card-date {
  font-size: 14px;
}
</style>
