<template>
  <div>
    <div v-if="post">
      <div v-if="owner" id="cp-container" v-show="editForm">
        <div id="cp-bg" @click="toggleEditForm"></div>
        <CreatePost ref="editPost" :post="post" @submitRequest="updatePost" />
      </div>
      <div v-else id="cp-container" v-show="rentalForm">
        <div id="cp-bg" @click="toggleRentalForm"></div>
        <RentalForm :post="post" @submitRequest="toggleRentalForm" />
      </div>
      <!--    zoom in viewer    -->
      <div>
        <vue-easy-lightbox
          escDisabled
          moveDisabled
          :visible="visible"
          :imgs="slides"
          :index="index"
          @hide="handleHide"
        ></vue-easy-lightbox>
      </div>
      <div id="pd-container">
        <div id="center-content-grid">
          <!--product details-->
          <div class="product-details-left">
            <!--images slider-->
            <div class="img-slider" v-if="slides.length > 0">
              <carousel>
                <slide
                  v-for="slide in slides"
                  :key="slide"
                  @click="showMultiple"
                >
                  <img
                    id="carousel-image"
                    alt="broken test img"
                    v-bind:src="slide"
                  />
                </slide>
                <template #addons>
                  <navigation />
                </template>
              </carousel>
              <div id="picture-text-bottom">
                Trykk på bilde for forhåndsvisning
              </div>
            </div>

            <div class="content-container">
              <div id="post-title">
                <h1>{{ post.title }}</h1>
                <div id="multiple-buttons" v-if="loggedIn">
                  <button
                    v-if="loggedIn"
                    class="input-button"
                    @click="routeToLogin"
                  >
                    Logg inn for å sende leieforespørsel
                  </button>
                </div>
                <div v-else-if="owner" class="button-container">
                  <div id="multiple-buttons">
                    <button class="input-button" @click="toggleEditForm">
                      Rediger annonse
                    </button>
                    <button class="input-button" @click="deleteThisPost">
                      Slett annonse
                    </button>
                  </div>
                </div>
                <div id="multiple-buttons" v-else>
                  <button class="input-button" @click="toggleRentalForm">
                    Send leieforespørsel
                  </button>
                </div>
              </div>
              <div id="price-container">
                <div id="price-view">
                  <div id="price-title-view">{{ post.pricePerDay }}</div>
                  <div id="price-per-day">kr/dag</div>
                </div>
              </div>
              <!-- Description !-->
              <div id="product-description-container">
                {{ post.description }}
              </div>
            </div>
          </div>

          <div class="product-details-right">
            <!--map-->
            <div class="content-container">
              <h3>Hentested</h3>
              <div id="postview-map">
                <MapShow
                  :locationObject="this.post.location"
                  :width="'300px'"
                  :height="'300px'"
                />
              </div>
            </div>

            <!--profile details-->
            <div class="content-container">
              <h3>Utleier</h3>
              <div class="product-description" v-if="post.organization != null">
                <p>
                  <span class="bold">Bedrift: </span>
                  <a
                    @click="routeToOrgProfile"
                    class="link"
                    id="organization-name"
                    >{{ post.organization.orgName }}</a
                  >
                </p>
                <p>
                  <span class="bold">Navn:</span>
                  {{ post.user.firstName + " " + post.user.lastName }}
                </p>
                <p><span class="bold">Epost:</span> {{ post.user.email }}</p>
              </div>
              <div class="product-description" v-else-if="post.user != null">
                <p>
                  <span class="bold">Navn: </span>
                  <a @click="routeToUserProfile" class="link" id="User-name">{{
                    post.user.firstName + " " + post.user.lastName
                  }}</a>
                </p>
                <p><span class="bold">Epost:</span> {{ post.user.email }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Carousel, Slide, Navigation } from "vue3-carousel";
import RentalForm from "../components/rental/RentalForm.vue";
import CreatePost from "../components/post/CreatePost.vue";
import { getPost, deletePost } from "@/service/PostService";
import MapShow from "@/components/map/MapShow.vue";

export default {
  name: "PostView.vue",
  components: {
    Carousel,
    Slide,
    Navigation,
    RentalForm,
    CreatePost,
    MapShow,
  },
  props: ["id"],
  data() {
    return {
      post: {},
      slides: [],
      testImage: "@/assets/images/socks.jpg",
      rentalForm: false,
      editForm: false,
      owner: false,
      loggedIn: false,
      visible: false,
      index: 0, // default: 0
    };
  },
  async created() {
    try {
      // load posts
      const response = await getPost(this.$route.params.id);
      this.post = response.data;

      // load and format pictures
      this.formatPictures(this.post.pictures);

      this.loggedIn = this.$store.getters.getUser.id == null;
      this.owner = this.post.user.id === this.$store.getters.getUser.id;
    } catch (error) {
      console.log(error);
    }
  },
  methods: {
    formatPictures(pictures) {
      this.slides = [];
      for (let i = 0; i < pictures.length; i++) {
        if (pictures[i].file) {
          this.slides.push(pictures[i].file.replace('"', "").replace('"', ""));
        }
      }
    },
    toggleRentalForm() {
      this.rentalForm = !this.rentalForm;
    },
    toggleEditForm() {
      this.editForm = !this.editForm;
    },
    routeToUserProfile() {
      this.$router.push("/userProfile/" + this.post.user.id);
    },
    routeToOrgProfile() {
      this.$router.push("/orgProfile/" + this.post.organization.orgNum);
    },
    routeToLogin() {
      this.$router.push("/signin");
    },
    // Updates a post.
    updatePost(value) {
      this.toggleEditForm();
      this.post = value;
      this.formatPictures(this.post.pictures);
      this.$forceUpdate();
    },
    // Deletes a post.
    async deleteThisPost() {
      try {
        await deletePost(this.post.postId);
        this.$notify({
          type: "success",
          title: "Sletting gjennomført",
          text: "Annonsen ble slettet",
        });
        this.$router.push("/");
      } catch (error) {
        console.log(error);
      }
    },
    showMultiple() {
      this.index = 1; // index of slides
      this.show();
    },
    show() {
      this.visible = true;
    },
    handleHide() {
      this.visible = false;
    },
  },
};
</script>

<style scoped>
@import "~vue3-carousel/dist/carousel.css";
.img-slider {
  /* vertical | horizontal */
  padding: 3% 5%;
  max-width: 600px;
}
.img-slider:hover {
  cursor: pointer;
}

#picture-text-bottom {
  font-size: 15px;
}

#carousel-image {
  max-height: 400px;
  max-width: 600px;
}

.product-details-right {
  grid-column: 2/3;
}

.product-details-left {
  grid-column: 1/2;
  float: left;
}

#product-description-container {
  display: flex;
  justify-self: start;
  margin-top: 30px;
}

.product-description {
  text-align: start;
}

#view-spacer {
  background-color: var(--grey);
  height: 1px;
  margin: 5px 0px;
  border-radius: var(--standard-border-radius);
}

.input-button {
  max-height: 35px;
  height: 35px;
  margin-left: 30px;
  font-size: 14px;
}

.button-container {
  display: flex;
}

#multiple-buttons {
  float: left;
  align-content: center;
  align-self: center;
}

.bold {
  font-weight: bold;
}

#pd-container {
  display: flex;
  width: 100%;
  justify-content: center;
}

#center-content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  max-width: 1000px;
}

#cp-container {
  position: fixed;
  top: 0;
  height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}
#cp-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgb(0, 0, 0, 0.8);
  z-index: 5;
}

#price-container {
  display: grid;
  grid-template-rows: 1fr;
  justify-content: start;
  justify-self: start;
}

#price-view {
  font-size: 25px;
  float: left;
  grid-row: 1/2;
  border: 1px solid var(--light-base);
  padding: 10px 25px;
  border-radius: var(--standard-border-radius);
}

#price-per-day {
  font-size: 15px;
}

#price-title-view {
  display: flex;
  font-weight: bold;
  justify-self: start;
}

.content-container {
  padding: 20px;
  justify-content: center;
}

#post-title {
  display: flex;
  justify-self: start;
}
</style>
