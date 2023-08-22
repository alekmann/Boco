<template>
  <img alt="logo" class="logo" src="@/assets/boco.svg" />
  <div class="form-container">
    <h1>Pålogging</h1>
    <Form @submit="onSubmit" v-slot="{ meta }">
      <TextInput
        label="Epost"
        name="email"
        placeholder="epost"
        type="email"
        rules="required|email"
        error-message="Vennligst skriv en gyldig epost"
        data-testid="input-email"
        validate-on-input
      />
      <TextInput
        label="Passord"
        name="password"
        placeholder="passord"
        type="password"
        rules="required"
        error-message="Vennligst skriv et passord"
        validate-on-input
      />
      <button :disabled="!meta.valid" data-testid="login-button">
        Logg inn
      </button>
    </Form>
    <p
      v-if="!this.correctPassword"
      style="color: red"
      data-testid="invalid-inputs"
    >
      > Ugyldig epost eller passord
    </p>
    <p style="margin-bottom: 10px">
      Har du ikke en bruker?
      <router-link as="a" class="link" to="register">Registrer deg</router-link>
    </p>
  </div>
</template>

<script>
import { Form } from "vee-validate";
import TextInput from "../components/form/TextInput";
import { login } from "@/service/AuthenticationService";

export default {
  name: "Login",
  data() {
    return {
      email: "",
      password: "",
      correctPassword: true,
    };
  },

  components: {
    Form,
    TextInput,
  },

  methods: {
    async onSubmit(value) {
      this.correctPassword = true;
      const user = {
        email: value.email,
        password: value.password,
      };
      try {
        // Logs in user and saves access token
        let response = await login(user);
        this.$store.dispatch("saveAccessToken", response.data.accessToken);
        this.$store.dispatch("fetchUser");

        // Route to frontpage
        await this.$router.push({ name: "Home" });
      } catch (error) {
        if (error.response.status === 403) {
          this.correctPassword = false;
        } else {
          console.log(error);
        }
      }
    },
  },
};
</script>
<style scoped src="@/assets/styles/forms.css" />
