<template>
  <div>
    <h3>{{ header }}</h3>
    <Form @submit="onSubmit" v-slot="{ meta }">
      <TextInput
        :label="label"
        :rules="rules"
        :name="name"
        :placeholder="placeholder"
        :type="type"
        :error-message="errorMessage"
        validate-on-input
      />
      <button class="button" :disabled="!meta.valid">{{ buttonText }}</button>
    </Form>
    <p v-show="this.editSuccess" style="color: black">{{ successText }}</p>
  </div>
</template>
<script>
import { Form } from "vee-validate";
import TextInput from "@/components/form/TextInput";

export default {
  props: {
    header: String,
    label: String,
    rules: String,
    name: String,
    placeholder: String,
    type: String,
    errorMessage: String,
    buttonText: String,
    editSuccess: Boolean,
    successText: String,
    submitFunction: Function,
  },

  components: {
    Form,
    TextInput,
  },

  // Takes in a function when used.
  methods: {
    onSubmit(value) {
      this.submitFunction(value);
    },
  },
};
</script>
<style scoped></style>
