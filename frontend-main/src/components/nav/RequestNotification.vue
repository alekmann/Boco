<template>
  <div id="rn-container">
    <div id="rn-image-container" v-if="postPicture">
      <img
        id="rn-post-img"
        class="pictures"
        alt="Post image"
        v-bind:src="postPicture"
      />
      <img
        id="rn-requester-img"
        class="pictures"
        alt="Requesting user image"
        v-bind:src="requesterPicture"
      />
      <font-awesome-icon id="rn-rent-icon" icon="arrow-right-arrow-left" />
    </div>
    <!-- Notification icon !-->
    <div v-else id="rn-no-image-container">
      <font-awesome-icon id="rn-no-img" icon="image" />
      <img
        id="rn-requester-img"
        class="pictures"
        alt="Requesting user image"
        v-bind:src="requesterPicture"
      />
      <font-awesome-icon id="rn-rent-icon" icon="arrow-right-arrow-left" />
    </div>
    <!-- Notification Main Content !-->
    <section id="rn-info">
      <p id="rn-info">
        <a @click="routeToUserProfile" class="link" id="rn-requester-name">{{
          request.organizationCustomer
            ? this.request.organizationCustomer.nickname + " "
            : this.getCustomerName
        }}</a>
        <span> ønsker å leie</span> <br />
      </p>
      <!-- Dates !-->
      <div class="rn-container">
        <p>Periode:</p>
        <strong>
          {{ formatDate(this.request.fromDate) }} -
          {{ formatDate(this.request.toDate) }}
        </strong>
      </div>
      <!-- Total price !-->
      <div class="rn-container">
        <p>Total:</p>
        <strong id="rn-sum">{{ this.request.totalPrice }} kr</strong>
      </div>
      <div v-show="this.request.post.inventory > 1" class="rn-container">
        <p>Antall:</p>
        <strong id="rn-sum">{{ this.request.amount }}</strong>
      </div>
    </section>
    <!-- Accept / Decline !-->
    <section id="rn-buttons">
      <font-awesome-icon id="rn-accept" icon="check" @click="answer(true)" />
      <font-awesome-icon id="rn-decline" icon="xmark" @click="answer(false)" />
    </section>
  </div>
</template>

<script>
import { answerRequest } from "@/service/RentalService.js";
export default {
  name: "RequestNotification",
  props: {
    request: {},
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
    //Used when answering request in notification.
    async answer(approve) {
      try {
        await answerRequest(this.request.rentalId, approve);
        this.$emit("requestAnswered", this.request.rentalId);

        if (approve) {
          this.$notify({
            type: "success",
            title: "Forespørsel godkjent",
            text: "Du godkjente leieforepørselen fra " + this.getCustomerName,
          });
        } else {
          this.$notify({
            type: "info",
            title: "Forespørsel avslått",
            text: "Du avslo leieforepørselen fra " + this.getCustomerName,
          });
        }
      } catch (err) {
        console.log(err);
      }
    },
    routeToUserProfile() {
      if (this.request.organizationCustomer) {
        this.$router.push(
          "/orgProfile/" + this.request.organizationCustomer.orgName
        );
      } else {
        this.$router.push("/userProfile/" + this.request.customer.id);
      }
    },
  },
  computed: {
    getCustomerName() {
      return (
        this.request.customer.firstName + " " + this.request.customer.lastName
      );
    },
    requesterPicture() {
      if (this.request.organizationCustomer) {
        if (this.request.organizationCustomer.profilePicture) {
          return this.request.organizationCustomer.profilePicture;
        } else {
          return require("@/assets/images/userIcon.jpeg");
        }
      } else {
        if (this.request.customer.profilePicture) {
          return this.request.customer.profilePicture;
        } else {
          return require("@/assets/images/userIcon.jpeg");
        }
      }
    },
    postPicture() {
      if (this.request.post.pictures.length !== 0) {
        return this.request.post.pictures[0].file
          .replace('"', "")
          .replace('"', "");
      } else {
        return null;
      }
    },
  },
};
</script>

<style scoped>
p {
  margin: 0;
}

#rn-container {
  display: grid;
  grid-template-columns: auto 1fr;
  grid-template-areas: "image info" "buttons buttons";
  align-items: center;
  justify-content: right;
  flex-direction: row;
  gap: 10px;
  padding: 10px;
}

#rn-image-container,
#rn-no-image-container {
  grid-area: image;
  display: flex;
  align-items: center;
  justify-items: left;
  flex-wrap: nowrap;
  height: 40px;
  width: 75px;
}

#rn-post-img,
#rn-requester-img,
#rn-no-img {
  position: absolute;
  margin: 0;
  height: 40px;
  width: 40px;
  border-radius: 100%;
}

#rn-post-img,
#rn-no-img {
  margin-left: 35px;
}

#rn-no-img {
  overflow: hidden;
  border-radius: 100%;
  object-fit: cover;
}

#rn-requester-img {
  margin-right: auto;
}

#rn-requester-name {
  text-decoration: none;
  color: var(--light-blue);
}

#rn-requester-name:hover {
  text-decoration: none;
  color: var(--baby-blue);
  text-decoration: underline;
}

#rn-rent-icon {
  font-size: 15px;
  filter: drop-shadow(0 0 1px rgba(0, 0, 0, 0.4));
  color: var(--dark-blue);
  margin-left: auto;
  margin-right: auto;
}

#rn-rent-icon > path {
  fill: none;
  stroke: white;
  stroke-width: 10px;
  stroke-dasharray: 2, 2;
  stroke-linejoin: round;
}

#rn-info {
  grid-area: info;
  text-align: left;
  margin-right: 10px;
}

#rn-sum {
  text-align: right;
  padding-left: 10px;
}

.rn-container {
  display: flex;
  justify-content: space-between;
}

#rn-buttons {
  grid-area: buttons;
  display: flex;
  justify-content: center;
}

#rn-accept,
#rn-decline {
  color: var(--calm-green);
  background-color: transparent;
  border-radius: 5px;
  font-size: 25px;
  padding: 3px 6px 3px 6px;
  transition: 150ms;
  cursor: pointer;
  width: 100%;
}

#rn-decline {
  color: var(--calm-red);
}

#rn-accept:hover,
#rn-decline:hover {
  color: var(--white);
}

#rn-accept:hover {
  background-color: var(--calm-green);
}

#rn-decline:hover {
  background-color: var(--calm-red);
}
</style>
