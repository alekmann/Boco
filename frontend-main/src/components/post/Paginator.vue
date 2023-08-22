<template>
  <div id="change-post-page">
    <div id="change-post-page-middle">
      <div id="left-arrow" @click="clickArrow(false)">
        <div id="click">
          <font-awesome-icon v-if="this.pageNumber !== 1" icon="angle-left" />
        </div>
      </div>
      <div id="page-number">Side {{ this.pageNumber }}</div>
      <div id="right-arrow" @click="clickArrow(true)">
        <div id="click">
          <font-awesome-icon
            v-if="this.pageNumber !== this.totalPages"
            icon="angle-right"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ["totalPages"],
  data() {
    return {
      pageNumber: 1,
    };
  },
  methods: {
    //Change page of page list.
    clickArrow(right) {
      if (right && this.pageNumber < this.totalPages) {
        ++this.pageNumber;
        this.$emit("changePage", this.pageNumber);
      } else if (!right && this.pageNumber - 1 > 0) {
        --this.pageNumber;
        this.$emit("changePage", this.pageNumber);
      }
      window.scrollTo(0, 0);
    },
  },
};
</script>

<style>
#change-post-page {
  padding: 10px 0 30px 0;
  justify-self: center;
  display: grid;
  bottom: 20px;
}
#change-post-page-middle {
  display: flex;
  justify-content: center;
  background-color: white;
  padding: 5px 0;
  border-radius: 20px;
}

#left-arrow,
#right-arrow {
  font-size: 18px;
  width: 40px;
}

#left-arrow:hover,
#right-arrow:hover {
  color: var(--chill-blue);
}

#click {
  cursor: pointer;
}

#page-number {
  align-self: center;
  font-size: 18px;
}
</style>
