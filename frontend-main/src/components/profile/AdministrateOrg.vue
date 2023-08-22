<template>
  <div class="profileGrid">
    <div class="sidebar">
      <div class="profile-sidebar-button" @click="pageScroll('bedrift')">
        Bedrift
      </div>
    </div>

    <div class="main">
      <div ref="bedrift">
        <h1>Bedrift</h1>
        <hr />
        <div ref="editOrg">
          <h2>Rediger bedrift</h2>
          <Form @submit="onSubmit" v-slot="{ meta }">
            <h3>Organisasjonsnummer</h3>
            <p>{{ orgNum }}</p>
            <TextInput
              label="Kallenavn til bedriften"
              rules="alpha_spaces|required"
              name="nickname"
              type="text"
              :value="nickname"
              error-message="Du må skrive et navn med bokstaver"
              validate-on-input
            />
            <button
              class="button"
              :disabled="!meta.valid"
              style="margin-top: 0px"
            >
              Rediger
            </button>
          </Form>
          <p v-show="this.EDIT_FAIL" style="color: red">
            Kunne ikke redigere organisasjonen
          </p>
          <p v-show="EDIT_SUCCESS" style="color: black">
            Organisasjonen er blitt redigert
          </p>
        </div>
      </div>

      <div class="profile-image-import">
        <div ref="bilde" class="profile-image-import-content">
          <h3>Bilde</h3>
          <input type="file" accept="image/*" @change="onPictureSelected" />
          <br />
          <button @click="submitPicture">Endre bilde</button>
        </div>
      </div>

      <div ref="slett">
        <h3>Slett konto</h3>
        <button
          id="delete-button"
          @click="this.onDelete()"
          class="button"
          style="margin-top: 0px"
        >
          Slett konto
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { Form } from "vee-validate";
import TextInput from "@/components/form/TextInput";
import {
  deleteOrg,
  editOrganization,
  changePicture,
} from "@/service/OrganizationService";

export default {
  name: "AdministrateOrg",

  components: {
    Form,
    TextInput,
  },

  data() {
    return {
      nickname: "",
      orgNum: null,
      EDIT_SUCCESS: false,
      EDIT_FAIL: false,
      images: [],
    };
  },
  created() {
    this.nickname = this.$store.getters.getOrganization.nickname;
    this.orgNum = this.$store.getters.getOrganization.orgNum;
  },

  methods: {
    onPictureSelected(event) {
      this.images = event.target.files;
    },
    // Add picture to organization.
    async submitPicture() {
      if (this.images.length != 0) {
        await this.sendPicture(this.orgNum);
        this.$store.dispatch("updateSelectedOrganization");
      } else {
        alert("Vennligst velg et bilde");
      }
    },
    //Changes picture of organisation.
    async sendPicture(orgNum) {
      try {
        const reader = new FileReader();
        reader.onload = function () {
          changePicture(orgNum, reader.result);
        };
        await reader.readAsDataURL(this.images[0]);
        this.$router.go(0);
      } catch (error) {
        console.log(error);
      }
    },
    async onSubmit(value) {
      const organization = {
        orgNum: this.orgNum,
        orgName: value.nickname,
      };

      try {
        // Edits organisation.
        await editOrganization(organization).then(
          this.$router.push("/profile/administrateorg")
        );
        this.EDIT_FAIL = false;
        this.EDIT_SUCCESS = true;
      } catch (error) {
        this.EDIT_SUCCESS = false;
        this.EDIT_FAIL = true;
        console.log(error);
      }
    },
    // Deletes organization, reloads home page as user after.
    async onDelete() {
      try {
        let text = "Er du sikker på at du vil slette bruker?";

        if (confirm(text) === false) {
          return;
        }
        await deleteOrg(this.orgNum).then((response) => {
          if (response.data === true) {
            this.$store.dispatch("setSelectedOrganization", null);
          }
        });
      } catch (error) {
        this.EDIT_SUCCESS = true;
        this.EDIT_FAIL = true;
        console.log(error);
      }
      await this.$router.push("/");
      await this.$router.go(0);
    },
  },
  // Navigation gard.
  beforeRouteEnter(to, from, next) {
    next((vm) => {
      if (
        from.path.substring(1, 8) !== "profile" &&
        !vm.$store.state.selectedOrganization
      ) {
        vm.$router.push({ path: "/profile" });
      }
    });
  },
};
</script>

<style scoped>
#delete-button {
  background-color: var(--calm-red);
}

button {
  margin-top: 30px;
}
</style>
