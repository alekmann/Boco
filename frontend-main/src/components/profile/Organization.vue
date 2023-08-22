<template>
  <div class="profileGrid">
    <div class="sidebar">
      <div class="profile-sidebar-button" @click="pageScroll('organisasjoner')">
        Organisasjoner
      </div>
      <div class="profile-sidebar-button" @click="pageScroll('opprett')">
        Opprett
      </div>
    </div>

    <div class="main">
      <div ref="organisasjoner">
        <h1>Organisasjoner</h1>
        <div id="org-list-placement">
          <OrgList :organizations="organizations" />
        </div>
      </div>
      <hr />
      <div style="padding-bottom: 40px" ref="opprett">
        <h2>Opprett organisasjon</h2>
        <Form @submit="onSubmit" v-slot="{ meta }">
          <div class="org-image-container">
            <div class="-org-image-label">
              <label id="org-pic-label">Organisasjonsbilde</label>
              <input
                id="pic-input"
                type="file"
                name="add-image"
                accept="image/*"
                @change="onPictureSelected"
              />
            </div>
          </div>
          <div class="input-container">
            <TextInput
              label="Organisasjonsnummer"
              rules="numeric|required"
              name="orgNumber"
              placeholder="organisasjonsnummer"
              type="text"
              error-message="Organisasjonen må ha 9 tall"
              validate-on-input
              :value="orgNum"
              @change="getOrgNameById"
            />
          </div>
          <div class="input-container">
            <TextInput
              label="Kallenavn på organisasjonen"
              rules="required"
              name="orgNick"
              placeholder="kallenavn"
              type="text"
              error-message="Organisasjonen må ha et navn"
              validate-on-input
            />
          </div>
          <label id="org-name-header">Organisasjonsnavn:</label>
          <p>{{ orgName }}</p>
          <button class="button" :disabled="!meta.valid">Lag bedrift</button>
        </Form>
        <p v-show="this.CREATE_FAIL" style="color: red">
          Denne organisasjonen finnes allerede
        </p>
        <p v-show="this.CREATE_SUCCESS" style="color: black">
          Bedriften ble vellykket lagt til!
        </p>
        <p v-show="this.GET_ORG_FAIL" style="color: red">
          Denne organisasjonen finnes ikke i Brønnøysundregisteret
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { Form } from "vee-validate";
import TextInput from "@/components/form/TextInput";
import {
  changePicture,
  createOrganization,
  getOrgName,
} from "@/service/OrganizationService";
import OrgList from "@/components/org/OrgList";

export default {
  name: "Organization",
  components: {
    OrgList,
    Form,
    TextInput,
  },

  data() {
    return {
      CREATE_SUCCESS: false,
      CREATE_FAIL: false,
      GET_ORG_FAIL: false,
      orgNum: null,
      orgName: "",
      organizations: this.$store.getters.getUser.organizations,
      image: [],
    };
  },
  methods: {
    onPictureSelected(event) {
      this.image = event.target.files;
    },
    async sendPicture(orgNum, image) {
      try {
        const reader = new FileReader();
        reader.onload = function () {
          changePicture(orgNum, reader.result);
        };
        await reader.readAsDataURL(image);
      } catch (error) {
        console.log(error);
      }
    },
    async onSubmit(value) {
      const organization = {
        orgNum: value.orgNumber,
        nickname: value.orgNick,
      };

      try {
        // Creates organisation and sends success or error message to user.
        await createOrganization(organization);
        if (this.image.length != 0) {
          await this.sendPicture(value.orgNumber, this.image[0]);
        }
        this.CREATE_FAIL = false;
        this.CREATE_SUCCESS = true;
        // Refreshes site.
        this.$router.go(0);
      } catch (error) {
        this.CREATE_SUCCESS = false;
        this.CREATE_FAIL = true;
        console.log(error);
      }
    },
    // Scrolls to chosen element.
    pageScroll(pageElement) {
      var element = this.$refs[pageElement];
      var top = element.offsetTop - 50;

      window.scrollTo(0, top);
    },
    // Collects organisation name from the Brønnøysund register.
    getOrgNameById(value) {
      const orgNumber = value.target.value;
      if (orgNumber.toString().length === 9) {
        getOrgName(orgNumber)
          .then((response) => {
            this.GET_ORG_FAIL = false;
            this.orgName = response.data;
          })
          .catch((error) => {
            this.GET_ORG_FAIL = true;
            this.orgName = "";
          });
      } else {
        this.orgName = "";
      }
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
  margin-top: 25px;
}
#org-list-placement {
  grid-row: 2/3;
}
#org-name-header {
  text-align: start;
}
#org-pic-label {
  float: left;
}
#pic-input {
  margin-top: 5px;
}
</style>
