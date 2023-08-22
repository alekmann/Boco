<template>
  <div id="jq">
    <h2>Leie {{ post.title }}</h2>
    <Form @submit="handleSubmit">
      <div class="input-container">
        <h3>Total pris</h3>
        <p>{{ post.pricePerDay * amountOfDays() * amount }} kr</p>
      </div>
      <div class="input-container">
        <h3>Sted</h3>

        <p v-if="post.location">
          {{
            post.location.address +
            ", " +
            post.location.postCode +
            " " +
            post.location.city +
            ", " +
            post.location.city
          }}
        </p>
      </div>
      <div class="input-container-date">
        <h3>Leieperiode</h3>
        <DatePicker
          v-model="range"
          :disabled-dates="unavailable"
          is-range
          show-weeknumbers
          title-position="left"
          :attributes="attrs"
        />
      </div>
      <div v-show="post.inventory > 1" class="input-container">
        <div id="inventory-check">
          <label>Ønsker du å leie flere?</label>
          <div class="test" v-if="showInventory">
            <input
              type="number"
              placeholder="beholdning"
              v-model="amount"
              @change="updateCalendar"
            />
          </div>
          <div v-else>
            <input v-model="showInventory" type="checkbox" @change="update" />
          </div>
        </div>
      </div>
      <button class="input button">Send leieforespørsel</button>
    </Form>
  </div>
</template>

<script>
import { Form } from "vee-validate";
import { createRentalRequest } from "@/service/RentalService";

export default {
  name: "RentForm",
  props: {
    post: {},
  },
  components: {
    Form,
  },
  data() {
    return {
      amount: 1,
      showInventory: false,
      range: {
        start: null,
        end: null,
      },
      unavailable: [
        {
          // Initial state is that all days before today are noe available
          start: null,
          end: ((d) => new Date(d.setDate(d.getDate() - 1)))(new Date()), // yesterday
        },
      ],
      attrs: [],
    };
  },
  methods: {
    // Updates calendar.
    updateCalendar(e) {
      // Define max
      if (e.target.value > this.post.inventory) {
        this.amount = this.post.inventory;
      }
      this.setupCalendar();
    },
    async handleSubmit(value) {
      const rentalRequest = {
        fromDate: this.range.start,
        toDate: this.range.end,
        totalPrice: this.post.pricePerDay * this.amountOfDays() * this.amount,
        organizationCustomer: this.$store.getters.getOrganization,
        amount: this.amount,
      };

      if (!this.range.start || !this.range.end) {
        this.$notify({
          type: "error",
          title: "En feil oppsto",
          text: "Leieperiode kan ikke stå tomt",
        });
      } else {
        try {
          // Creates the rental request
          await createRentalRequest(this.$route.params.id, rentalRequest);
          this.$emit("submitRequest");
          await this.$router.push("/");

          // Notification
          this.$notify({
            type: "success",
            title: "Leieforespørsel sendt",
            text: "Leie forespørselen på " + this.post.title + " ble sendt.",
          });
        } catch (error) {
          console.log(error);
          this.$notify({
            type: "error",
            title: "En feil oppsto",
            text: "Leie forespørselen ble ikke sendt.",
          });
        }
      }
    },
    // Calculates how many days the rental is for.
    amountOfDays() {
      if (!this.range.start) {
        return 0;
      }
      const date1 = this.range.start;
      const date2 = this.range.end;
      const diffTime = Math.abs(date2 - date1);
      return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
    },
    setupCalendar() {
      // reset
      this.unavailable = [
        {
          // Initial state is that all days before today are noe available
          start: null,
          end: ((d) => new Date(d.setDate(d.getDate() - 1)))(new Date()), // yesterday
        },
      ];
      this.attrs = [];

      // Iterate and change dates
      this.post.unavailable.map((e) => {
        if (this.amount > this.post.inventory - e.amount) {
          this.unavailable.push(e);
          this.attrs.push({
            highlight: {
              color: "red",
              fillMode: "light",
              style: {
                color: "green",
              },
            },
            dates: e,
          });
        }
      });
    },
  },
  beforeUpdate() {
    this.setupCalendar();
  },
};
</script>

<style scoped>
Form {
  width: 80%;
  justify-content: stretch;
}
.button {
  align-self: center;
  margin-bottom: 30px;
}
.input-container {
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}
.input-container-date {
  width: 300px;
  flex-direction: column;
  flex-wrap: wrap;
}
#jq {
  background-color: white;
  border-radius: 10px;
  min-width: 200px;
  width: 80%;
  max-width: 400px;
  min-height: 100px;
  max-height: 900px;
  display: flex;
  align-items: center;
  flex-direction: column;
  flex-wrap: wrap;
  z-index: 10;
}

#inventory-check {
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  width: 100%;
}

#inventory-check * {
  font-size: 12px;
  width: auto;
  text-align: left;
  padding: 0.1rem;
  min-height: 0;
}
</style>
