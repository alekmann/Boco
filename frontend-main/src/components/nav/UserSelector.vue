<template>
  <div class="user-widget">
    <img
      class="pictures"
      :style="selectStyling"
      v-bind:src="picture"
      alt="user image"
    />
    <p class="uw-name">{{ name }}</p>
    <div class="mark-container">
      <font-awesome-icon v-show="selected" class="checkmark" icon="check" />
    </div>
  </div>
</template>

<script>
export default {
  name: "UserSelector",
  props: {
    name: String,
    selected: Boolean,
    picture: null,
  },

  computed: {
    selectStyling() {
      return this.selected ? "border: solid 2px var(--green);" : "";
    },
  },
};
</script>

<style scoped>
.user-widget {
  display: flex;
  justify-content: left;
  flex-direction: row;
  gap: 10px;
  cursor: pointer;
  padding: 10px 2vw 10px 2vw;
}

.user-widget:hover {
  background-color: var(--chill-blue);
}

.pictures {
  height: 40px;
  width: 40px;
  border: solid 2px transparent;
}

.uw-name {
  font-weight: bold;
  color: white;
  margin: auto 0 auto 0;
  white-space: nowrap;
}

.mark-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  width: 40px;
  margin: auto 0 auto auto;
}

.checkmark {
  font-size: 20px;
  color: var(--green);
  width: 40px;
}
</style>
