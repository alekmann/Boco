<template>
  <h3>{{ header }}</h3>
  <Form @submit="onSubmit" v-slot="{ meta }">
    <TextInput
      v-for="(input, index) in this.inputs"
      :key="index"
      :label="input.label"
      :rules="input.rules"
      :name="input.name"
      :placeholder="input.placeholder"
      :type="input.type"
      :error-message="input.errorMessage"
      validate-on-input
    />
    <button class="button" :disabled="!meta.valid">{{ buttonText }}</button>
  </Form>
</template>
<script>
import { Form } from "vee-validate";
import TextInput from "@/components/form/TextInput";

export default {
  props: {
    header: String,
    submitFunction: Function,
    inputs: Array,
    buttonText: String,
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
