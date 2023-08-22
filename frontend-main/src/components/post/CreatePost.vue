<template>
  <div id="jq">
    <!-- MAP -->
    <div id="cp-container" v-if="showMap">
      <div id="cp-bg" @click="toggleMap"></div>
      <Map @hideMap="toggleMap" />
    </div>

    <h2 v-if="owner">Rediger annonse</h2>
    <h2 v-else>Legg til annonse</h2>
    <Form id="cp-form" v-slot="{ handleSubmit }" @submit.prevent>
      <TextInput
        label="Tittel"
        rules="required"
        name="title"
        :value="this.title"
        v-model="title"
        placeholder="tittel"
        type="text"
        error-message="Du må skrive en tittel"
      />
      <TextInput
        label="Pris per dag (NOK)"
        rules="required|numeric"
        name="pricePerDay"
        :value="this.pricePerDay.toString()"
        v-model="this.pricePerDay"
        placeholder="pris"
        type="text"
        error-message="Du må skrive en pris"
      />
      <div class="input-container" id="cp-category">
        <label>Kategori</label>
        <select name="category" v-model="category" class="input select">
          <option v-for="category in categories" :key="category">
            {{ category.name }}
          </option>
        </select>
      </div>
      <TextInput
        label="Beskrivelse"
        rules="required"
        name="description"
        :value="this.description"
        v-model="this.description"
        placeholder="beskrivelse"
        type="text"
        error-message="Du må skrive en beskrivelse"
      />
      <!-- velg sted knapp -->
      <div
        class="input-container"
        style="display: flex; flex-direction: column"
      >
        <label id="hentested-text">Hentested</label>
        <a class="link" id="address" @click="toggleMap">
          {{
            this.location
              ? location.address +
                ", " +
                location.postCode +
                " " +
                location.city +
                ", " +
                location.country
              : "Ingen adresse valgt"
          }}
        </a>
        <div v-if="locationErrorMessage" id="location-error-message">
          Hentested må legges inn
        </div>
      </div>
      <div class="input-container">
        <input
          type="file"
          multiple
          accept="image/jpeg"
          @change="onFileSelected"
        />
      </div>
      <div class="images-container">
        <div class="images-label">
          <label for="add-image">Bilder</label>
          <div id="import-file" class="button">
            Legg til
            <input
              type="file"
              name="add-image"
              multiple
              accept="image/jpeg"
              @change="onFileSelected"
            />
          </div>
        </div>
        <div v-if="images.length !== 0">
          <div class="files">
            <div class="file" v-for="image in images" :key="image">
              <div>{{ image.name }}</div>
              <div @click="removeFile(image)" class="file-remove">slett</div>
            </div>
          </div>
        </div>
        <div v-else>Ingen bilder...</div>
      </div>
      <div class="input-container">
        <div id="inventory-check">
          <label>Har du flere i beholdning?</label>
          <div v-if="showInventory">
            <input
              type="number"
              placeholder="beholdning"
              min="1"
              v-model="inventory"
            />
          </div>
          <div v-else>
            <input v-model="showInventory" type="checkbox" @change="update" />
          </div>
        </div>
      </div>
      <button
        v-if="owner"
        class="input button"
        @click="handleSubmit($event, onSubmit)"
      >
        Rediger
      </button>
      <button
        v-else
        class="input button"
        @click="handleSubmit($event, onSubmit)"
      >
        Lag annonse
      </button>
    </Form>
  </div>
</template>
<script>
import { Form } from "vee-validate";
import TextInput from "@/components/form/TextInput";
import { getAllCategories } from "@/service/CategoryService.js";
import { editPost, addPicture, addPost } from "@/service/PostService";
import { mapState } from "vuex";
import Map from "@/components/map/MapSelect.vue";

export default {
  name: "CreatePost",
  components: {
    TextInput,
    Form,
    Map,
  },
  props: {
    post: {},
  },
  computed: {
    ...mapState(["selectedOrganization"]),
  },
  data() {
    return {
      owner: false,
      title: this.post ? this.post.title : "",
      pricePerDay: this.post ? this.post.pricePerDay : 0,
      category: this.post ? this.post.category.name : "",
      description: this.post ? this.post.description : "",
      location: this.post ? this.post.location : "",
      locationErrorMessage: "",
      images: [],
      categories: {},
      showMap: false,
      inventory: 1,
      showInventory: false,
    };
  },
  methods: {
    // map
    toggleMap(location) {
      this.showMap = !this.showMap;
      this.location = location;
      if (this.location) {
        this.locationErrorMessage = "";
      }
    },
    async onSubmit(value, { resetForm }) {
      if (!this.location) {
        this.locationErrorMessage = "Hentested må legges inn";
        return;
      }
      // category
      let categoryObject = {
        id: "",
        name: "",
      };
      for (let i = 0; i < this.categories.length; i++) {
        if (this.categories[i].name === this.category) {
          categoryObject.name = this.categories[i].name;
          categoryObject.id = this.categories[i].id;
        }
      }

      // request
      let request = {
        postId: this.owner ? this.post.postId : null,
        title: value.title,
        pricePerDay: value.pricePerDay,
        category: categoryObject,
        location: this.location,
        description: value.description,
        organization: this.selectedOrganization,
        pictures: this.images,
        inventory: this.inventory,
      };

      if (this.owner) {
        //Edit post if you are the owner.
        try {
          const response = await editPost(request);
          this.$emit("submitRequest", response.data);
          this.$notify({
            type: "success",
            title: "Endringer lagret",
            text: "Endringene på " + this.post.title + " ble lagret.",
          });
        } catch (err) {
          console.log(err);
          this.$notify({
            type: "error",
            title: "En feil oppsto",
            text: "Endringene på " + this.post.title + " ble ikke lagret.",
          });
        }
      } else {
        try {
          //Adds the post.
          const response = await addPost(request);
          this.postId = response.data;

          resetForm();
          this.$emit("postCreated");
          this.$router.push("/posts/" + this.postId);
        } catch (err) {
          console.log(err);
          this.$notify({
            type: "error",
            title: "En feil oppsto",
            text: "Annonsen ble ikke opprettet.",
          });
        }
      }
    },
    //Adds the file.
    onFileSelected(event) {
      let files = event.target.files;
      for (let i = 0; i < files.length; i++) {
        let file = files[i];

        const reader = new FileReader();
        reader.onload = () => {
          this.images.push({
            name: file.name,
            file: reader.result,
          });
        };
        reader.readAsDataURL(file);
      }
    },
    //Removes file.
    removeFile(file) {
      this.images = this.images.filter((image) => {
        return file !== image;
      });
    },
    //Shows option to add inventory.
    update(e) {
      this.showInventory = e;
    },
    //Submits image.
    async submitImage(image, id) {
      const reader = new FileReader();

      reader.onload = function () {
        addPicture(
          {
            file: reader.result,
            name: image.name,
          },
          id
        );
      };
      reader.readAsDataURL(image);
    },
  },
  async created() {
    //Collects categories.
    try {
      const response = await getAllCategories();
      this.categories = response.data;
    } catch (error) {
      console.log(error);
    }

    if (this.post) {
      this.title = this.post.title;
      this.pricePerDay = this.post.pricePerDay;
      this.description = this.post.description;
      this.location = this.post.location;
      this.category = this.post.category.name;
      this.owner = true;
      this.images = this.post.pictures;
      this.inventory = this.post.inventory;
      this.showInventory = this.inventory > 1;
    }
  },
};
</script>

<style scoped>
Form {
  width: 80%;
  justify-content: stretch;
}

input[type="file"] {
  opacity: 0;
  position: absolute;
  cursor: pointer;
  top: 0;
  right: 0;
  font-size: 2rem;
}

#hentested-text {
  float: left;
}

input[type="file"]::file-selector-button {
  margin-top: 5px;
  padding: 5px 10px 5px 10px;
}

#import-file {
  position: relative;
  overflow: hidden;
  margin: 0;
  padding: 0.2rem 0.5rem;
}

.images-container {
  text-align: left;
}

.images-label {
  display: flex;
  justify-content: space-between;
}

.file {
  display: flex;
  justify-content: space-between;
  padding: 0.1rem 0.5rem;
  border-radius: 5px;
}

.file:hover {
  background: #f3f3f3;
  transition-duration: 0.2s;
}

.file-remove {
  color: var(--red);
  cursor: pointer;
  opacity: 0.7;
}

.file-remove:hover {
  transition-duration: 0.2s;
  opacity: 1;
}

.files {
  overflow: auto;
  max-height: 100px;
  margin-top: 1rem;
  margin-left: -0.5rem;
}

.input-container {
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

.button {
  align-self: center;
  margin-bottom: 30px;
}

#cp-category {
  text-align: left;
}

#jq {
  background-color: white;
  border-radius: 10px;
  min-width: 200px;
  width: 80%;
  max-width: 400px;
  min-height: 100px;
  max-height: 800px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  z-index: 10;
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

#inventory-check {
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  width: 100%;
}

#inventory-check * {
  font-size: 12px;
  width: auto;
  text-align: left;
  padding: 0.1rem;
  min-height: 0;
}

#location-error-message {
  color: red;
}
</style>
