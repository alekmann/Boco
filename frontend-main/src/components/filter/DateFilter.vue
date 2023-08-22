<template>
  <div class="date-container">
    <!-- Calendar !-->
    <DatePicker
      v-model="range"
      :columns="$screens({ default: 1, lg: 2 })"
      :step="1"
      is-range
      class="date-picker"
      @click="this.$emit('datesSelected', range)"
    >
      <!-- Input Fields !-->
      <template #default="{ inputValue, togglePopover }">
        <input
          readonly
          :value="inputValue.start"
          @click="togglePopover()"
          placeholder="fra"
          ref="date-from"
        />
        <input
          readonly
          :value="inputValue.end"
          @click="togglePopover()"
          placeholder="til"
          ref="date-to"
        />
      </template>
    </DatePicker>
  </div>
</template>

<script>
export default {
  data() {
    return {
      viewCalendar: false,
      range: {
        start: null,
        end: null,
      },
    };
  },
  methods: {
    reset() {
      this.range = null;
      this.$refs["date-from"].value = null;
      this.$refs["date-to"].value = null;
    },
  },
};
</script>

<style scoped>
.date-picker {
  display: flex;
  justify-content: space-between;
}

.date-picker * {
  width: 48%;
  cursor: pointer;
}
</style>
