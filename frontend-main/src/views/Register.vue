<template>
  <!--  Registration Successful Message -->
  <MessageCard v-show="success" />

  <!-- MAP -->
  <div id="cp-container" v-if="showMap">
    <div id="cp-bg" @click="toggleMap"></div>
    <MapSelect @hideMap="toggleMap" />
  </div>

  <!--  Registration fields  -->
  <div v-show="!success && !showMap">
    <img alt="logo" class="logo" src="@/assets/boco.svg" />
    <div class="form-container">
      <h1>Registrer bruker</h1>
      <Form @submit="onSubmit" v-slot="{ meta }">
        <TextInput
          label="Email"
          name="email"
          placeholder="brukernavn@domene.org"
          type="text"
          rules="required|email"
          error-message="Ugyldig epost"
        />
        <TextInput
          label="Fornavn"
          name="firstName"
          placeholder="fornavn"
          type="text"
          rules="required|alpha_spaces|min_max:1,45"
          error-message="Etternavn kan kun inneholde bokstaver"
        />
        <TextInput
          label="Etternavn"
          name="lastName"
          placeholder="etternavn"
          type="text"
          rules="required|alpha_spaces"
          error-message="Etternavn kan kun inneholde bokstaver"
        />
        <!-- velg sted knapp -->
        <div class="input-container">
          <label id="hentested-text">Adresse</label>
          <a class="link" id="address" @click="toggleMap">
            {{
              this.location
                ? location.address +
                  ", " +
                  location.postCode +
                  " " +
                  location.city +
                  ", " +
                  location.country
                : "Ingen adresse valgt"
            }}
          </a>
        </div>
        <!-- velg sted knapp -->
        <TextInput
          label="Passord"
          name="password"
          placeholder="passord"
          type="password"
          rules="required"
          error-message="Passord kan ikke være tomt"
        />
        <TextInput
          label="Bekreft passord"
          name="confirm"
          placeholder="passord"
          type="password"
          rules="required|confirmed:@password"
          error-message="Passordene samstemmer ikke"
          validate-on-input
        />
        <button class="button" :disabled="!meta.valid">Registrer deg</button>
      </Form>
      <p style="margin-bottom: 10px">
        Har du allerede en bruker?
        <router-link as="a" class="link" to="/signin">Logg inn</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { Form } from "vee-validate";
import TextInput from "@/components/form/TextInput";
import { register } from "@/service/AuthenticationService";
import MessageCard from "@/components/alert/MessageCard";
import MapSelect from "@/components/map/MapSelect.vue";

export default {
  name: "Login",
  components: {
    TextInput,
    Form,
    MessageCard,
    MapSelect,
  },
  data() {
    return {
      success: false,
      location: null,
      showMap: false,
    };
  },
  methods: {
    toggleMap(location) {
      this.showMap = !this.showMap;
      this.location = location;
    },
    async onSubmit(value) {
      const user = {
        email: value.email,
        firstName: value.firstName,
        lastName: value.lastName,
        password: value.password,
        location: this.location,
      };
      try {
        // Registers the user.
        await register(user);
        this.success = true;
      } catch (error) {
        console.log(error);
      }
    },
  },
};
</script>
<style scoped src="@/assets/styles/forms.css"></style>
