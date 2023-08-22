<template>
  <div class="nav-button-container">
    <div class="icon-container">
      <font-awesome-icon :icon="icon" class="icon" fixed-width />
    </div>
    <div class="tooltip" v-show="showTooltip">
      <p class="tooltip-text">{{ tooltipText }}</p>
    </div>
  </div>
</template>
<script>
export default {
  name: "NavButton",
  data() {
    return {
      showTooltip: true,
    };
  },
  props: {
    icon: String,
    tooltipText: String,
  },
};
</script>

<style scoped>
.nav-button-container {
  display: flex;
  justify-content: center;
  cursor: pointer;
}

.icon {
  font-size: 28px;
  transition: 0.2s;
  color: var(--white);
}

.icon-container {
  display: flex;
  justify-content: center;
  margin: auto 0 auto 0;
}

.icon-container:after {
  content: "";
  display: block;
  position: absolute;
  margin-top: 32px;
  height: 2px;
  width: 0;
  background: transparent;
  -webkit-transition: 0.2s;
  transition: 0.1s;
  border-radius: 2px;
}

.icon:hover {
  color: #ffffff;
}

.icon-container:hover:after {
  width: 35px;
  background: var(--white);
}

.nav-button-container:hover > .tooltip {
  visibility: visible;
}

.tooltip {
  visibility: hidden;
  position: absolute;
  padding: 4px 8px 4px 8px;
  background-color: var(--light-base);
  margin-top: 55px;
  border-radius: 5px;
  z-index: 200;
}

.tooltip-text {
  color: var(--white);
  white-space: nowrap;
  margin: 0;
  user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  -webkit-user-select: none;
}
</style>
