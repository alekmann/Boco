<template>
  <div id="post-card" @click="clickCard">
    <img
      v-if="this.image"
      id="post-card-image"
      v-bind:src="this.image"
      alt="post image"
    />
    <div v-else id="no-image-container">
      <font-awesome-icon id="no-image" icon="image" />
    </div>

    <div id="post-info">
      <h2 id="title">{{ post.title }}</h2>
      <section id="price">
        <div>{{ post.pricePerDay }}<span id="crowns">kr</span></div>
        <div id="denominator">per dag</div>
      </section>
      <section id="description">{{ post.description }}</section>
      <div v-if="post.location" id="location">
        <font-awesome-icon id="location-icon" icon="location-dot" />
        <p>{{ post.location.city }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    post: {},
  },
  data() {
    return {
      image: "",
    };
  },
  methods: {
    clickCard() {
      // Open postView page with id
      this.$router.push(`/posts/${this.post.postId}`);
    },
  },
  async created() {
    // Replaces tag marks.
    if (this.post.pictures.length !== 0) {
      this.image = this.post.pictures[0].file.replace('"', "").replace('"', "");
    }
  },
};
</script>

<style>
#post-card {
  display: grid;
  grid-template-areas: "image" "info";
  grid-auto-rows: auto 1fr;
  border: solid 1px var(--light-grey);
  cursor: pointer;
  transition: all 0.15s;
  margin: 0;
}

#no-image-container {
  min-width: 250px;
  min-height: 250px;
  display: grid;
}

#no-image {
  font-size: 100px;
  align-self: center;
  justify-self: center;
  color: var(--light-grey);
}

#post-card:hover {
  border-color: var(--light-blue);
}

#post-card:hover > #post-info {
  background-color: var(--baby-blue);
}

#post-card-image,
#post-card {
  border-radius: var(--standard-border-radius);
  width: 250px;
}

#post-card-image {
  position: relative;
  aspect-ratio: 1/1;
  grid-area: image;
  object-fit: cover;
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

#post-info {
  display: grid;
  grid-area: info;
  grid-template-areas: "title price" "description description" "tags tags";
  grid-auto-columns: 1fr auto;
  background-color: white;
  padding: 5px 10px;
  border-bottom-left-radius: var(--standard-border-radius);
  border-bottom-right-radius: var(--standard-border-radius);
}

#title,
#post-info {
  color: var(--base);
}

#title {
  font-size: 18px;
  font-weight: 600;
  margin: auto 5px auto 0;
  grid-area: title;
  text-align: left;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

#price {
  font-size: 16px;
  font-weight: 600;
  color: var(--dark-grey);
  grid-area: price;
  text-align: right;
}

#crowns {
  font-size: 12px;
  margin-left: 2px;
}

#denominator {
  font-size: 12px;
  color: var(--dark-grey);
  font-weight: lighter;
  margin-top: -6px;
}

#description {
  font-size: 15px;
  grid-area: description;
  text-align: left;
  color: var(--dark-grey);
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 100%;
}

#location {
  grid-area: tags;
  font-size: 14px;
  font-weight: 100;
  display: inline-flex;
  flex-wrap: nowrap;
  padding-top: 5px;
  margin-top: auto;
}

#location > p {
  margin: 0 0 0 3px;
}

#location-icon {
  margin-top: 3px;
  font-size: 11px;
  color: var(--chill-blue);
}
</style>
