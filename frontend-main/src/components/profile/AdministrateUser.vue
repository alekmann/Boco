<template>
  <div class="profileGrid">
    <div class="sidebar">
      <div class="profile-sidebar-button" @click="pageScroll('konto')">
        Konto
      </div>
      <div class="profile-sidebar-button" @click="pageScroll('passord')">
        Endre passord
      </div>
      <div class="profile-sidebar-button" @click="pageScroll('bilde')">
        Endre profilbilde
      </div>
      <div class="profile-sidebar-button" @click="pageScroll('slett')">
        Slett konto
      </div>
    </div>

    <div class="main">
      <div ref="konto">
        <h1>Konto</h1>

        <hr />

        <div>
          <SingleFieldForm
            header="Navn"
            label="Endre navn"
            rules="required|alpha_spaces"
            name="name"
            placeholder="nytt navn"
            type="text"
            error-message="Navn må være bokstaver"
            validate-on-input
            button-text="Endre navn"
            :edit-success="editSuccessName"
            success-text="Ditt navn ble endret"
            :submit-function="onSubmitName"
          />
          <p v-show="editFailName" style="color: var(--calm-red)">
            Du må skrive fornavn og etternavn
          </p>
        </div>
        <hr />

        <div ref="passord">
          <MultipleFieldForm
            header="Passord"
            :inputs="passwordFormFields"
            button-text="Endre passord"
            :submit-function="onSubmitPassword"
          />
          <p v-show="editFailPassword" style="color: var(--calm-red)">
            Feil passord
          </p>
          <p v-show="editSuccessPassword" style="color: black">
            Passordet er endret
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
import SingleFieldForm from "@/components/form/SingleFieldForm";
import {
  changePassword,
  changePicture,
  deleteUser,
  editName,
} from "@/service/AdministrateUserService";
import { logout } from "@/service/AuthenticationService";
import MultipleFieldForm from "@/components/form/MultipleFieldForm";

export default {
  name: "AdministrateUser",
  components: {
    SingleFieldForm,
    MultipleFieldForm,
  },

  data() {
    return {
      editSuccessName: false,
      editSuccessEmail: false,
      editSuccessPassword: false,
      editFailName: false,
      editFailPassword: false,
      passwordFormFields: [
        {
          label: "Nåværende passord",
          rules: "required",
          name: "oldPassword",
          placeholder: "passord",
          type: "password",
          errorMessage: "Du må skrive passordet ditt",
        },
        {
          label: "Nytt passord",
          rules: "required",
          name: "newPassword",
          placeholder: "passord",
          type: "password",
          errorMessage: "Du må skrive et nytt passord",
        },
        {
          label: "Bekreft nytt passord",
          rules: "required|confirmed:@newPassword",
          name: "repeatedPassword",
          placeholder: "passord",
          type: "password",
          errorMessage: "Passordene samstemmer ikke",
        },
      ],
      images: [],
    };
  },
  methods: {
    onPictureSelected(event) {
      this.images = event.target.files;
    },
    // Submits picture.
    submitPicture() {
      if (this.images.length != 0) {
        try {
          const reader = new FileReader();

          // Defines what to do when reader loads.
          reader.onload = function () {
            changePicture(reader.result);
          };
          reader.readAsDataURL(this.images[0]);
          this.$router.go(0);
        } catch (error) {
          console.log(error);
        }
      } else {
        alert("Vennligst velg et bilde");
      }
    },
    // Submits a name.
    async onSubmitName(value) {
      if (value.name.toString().split(" ").length === 1) {
        // Shows error message if name don't contain last name.
        this.editFailName = true;
        return;
      }

      this.editFailName = false;
      // Splits name to send a first name and last name to backend.
      const nameArray = value.name.toString().split(" ");
      try {
        await editName(nameArray.toString());
        await this.$router.go(0);
        // Shows message that edit was done successfully.
        this.editSuccessName = true;
      } catch (error) {
        console.log(error);
      }
    },
    // Changes password and gives either success or error message to user.
    async onSubmitPassword(value) {
      try {
        await changePassword(value);
        this.editFailPassword = false;
        this.editSuccessPassword = true;
      } catch (error) {
        this.editSuccessPassword = false;
        this.editFailPassword = true;
        console.log(error);
      }
    },

    // Pop up window where you can choose if you wnat to delete or not.
    async onDelete() {
      let text = "Er du sikker på at du vil slette bruker?";

      if (confirm(text) === false) {
        return;
      }

      await deleteUser();
      this.$store.dispatch("saveAccessToken", undefined);
      await logout();
      // Pushes sign in page when user is logged out and deleted.
      await this.$router.push("/signin");
      await this.$router.go(0);
    },

    // Scrolls to chosen element.
    pageScroll(pageElement) {
      var element = this.$refs[pageElement];
      var top = element.offsetTop - 50;

      window.scrollTo(0, top);
    },
  },
  // Navigation gard.
  beforeRouteEnter(to, from, next) {
    next((vm) => {
      if (
        from.path.substring(1, 8) !== "profile" &&
        vm.$store.state.selectedOrganization
      ) {
        vm.$router.push({ path: "/profile" });
      }
    });
  },
};
</script>

<style scoped>
hr {
  margin-top: 15px;
}
button {
  margin-top: 30px;
}
#delete-button {
  background-color: var(--calm-red);
}
</style>
