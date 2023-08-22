<template>
  <div class="input-container">
    <label>{{ label }}</label>
    <Field
      class="input"
      :rules="rules"
      :name="name"
      :type="type"
      :placeholder="placeholder"
      :validateOnInput="validateOnInput"
      :value="value"
    />
    <span class="error">
      <ErrorMessage :name="name">{{ errorMessage }}</ErrorMessage>
    </span>
  </div>
</template>

<script>
import { Field, ErrorMessage } from "vee-validate";

export default {
  name: "TextInput",

  components: {
    Field,
    ErrorMessage,
  },

  props: {
    label: String,
    rules: String,
    name: String,
    type: String,
    placeholder: String,
    validateOnInput: Boolean,
    errorMessage: String,
    value: String,
  },
};
</script>

<style scoped>
hr {
  margin-top: 25px;
}

.input-container {
  width: 300px;
  display: flex;
  flex-wrap: wrap;
}
</style>
