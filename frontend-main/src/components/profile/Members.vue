<template>
  <div class="profileGrid">
    <!--    SIDEBAR     -->
    <div class="sidebar">
      <div v-show="isCurrentAdmin">
        <h3>Legg til bruker</h3>
        <Form id="Add-user-form" @submit="addNewMember" v-slot="{ meta }">
          <TextInput
            label="Epost"
            name="newMemberEmail"
            type="email"
            rules="required|email"
            error-message="Vennligst skriv en gyldig epost"
            validate-on-input
          />
          <div class="input-container">
            <label>Role</label>
            <select name="role" v-model="role" class="input select">
              <option>Admin</option>
              <option>Ansatt</option>
            </select>
          </div>
          <button :disabled="!meta.valid">Legg til</button>
        </Form>
      </div>
    </div>

    <!--    MAIN   -->
    <div class="main">
      <input
        type="text"
        class="search"
        placeholder="Søk på medlem..."
        v-model="search"
        @keyup.enter="searchMember"
      />
      <br /><br />
      <MemberList :members="members" :isCurrentAdmin="isCurrentAdmin" />
    </div>
  </div>
</template>

<script>
import { Form } from "vee-validate";
import TextInput from "@/components/form/TextInput";
import {
  addNewMember,
  getMembers,
  search,
} from "@/service/OrganizationService";
import MemberList from "@/components/org/MemberList";

export default {
  name: "Members",
  components: {
    Form,
    TextInput,
    MemberList,
  },
  data() {
    return {
      render: true,
      newMemberEmail: "",
      search: "",
      role: "",
      members: [],
      isCurrentAdmin: null,
    };
  },
  methods: {
    async addNewMember(value) {
      /**NewMember : {email, role, orgNum}*/
      let member = {
        email: value.newMemberEmail,
        role: this.role === "Admin" ? "ADMIN" : "EMPLOYEE",
        orgNum: this.$store.getters.getOrganization.orgNum,
      };
      try {
        await addNewMember(member);
        this.$router.go(0); // refresh the page
        this.$notify({
          type: "success",
          title: "Vellykket",
          text: `${member.email} ble lagt til`,
        });
      } catch (error) {
        console.log(error);
        this.$notify({
          type: "error",
          title: "Feilet",
          text: `${member.email} er ugyldig bruker`,
        });
      }
    },
    async searchMember() {
      try {
        let orgNum = this.$store.getters.getOrganization.orgNum;
        const response = await search(orgNum, this.search);
        this.members = this.filterResponse(response);

        // check the result to handle the notification
        let foundResult = this.members.length ? true : false;
        // handle the notification
        if (foundResult) {
          this.$notify({
            type: "success",
            title: "Vellykket",
            text: `Resultat funnet for ${this.search}`,
          });
        } else {
          this.$notify({
            type: "error",
            title: "Funnet ikke",
            text: `Ingen resultater for ${this.search}`,
          });
        }
      } catch (error) {
        console.log(error);
      }
    },
    /**
     * Filter the response => return (firstname, lastname, role, id) //id will be used with delete member
     */
    filterResponse(response) {
      return response.data.map(function (elem) {
        return {
          firstName: elem.user.firstName,
          lastName: elem.user.lastName,
          role: elem.organizationRole,
          id: elem.user.id,
          email: elem.user.email,
        };
      });
    },
    /**
     * Help method to check if the current user is admin or not.
     * @returns {boolean}
     */
    isCurrentUserAdmin() {
      let currentUserId = this.$store.getters.getUser.id;
      for (let i = 0; i < this.members.length; i++) {
        if (this.members[i].id === currentUserId) {
          return this.members[i].role === "ADMIN";
        }
      }
    },
  },
  async created() {
    try {
      let orgNum = this.$store.getters.getOrganization.orgNum;
      const response = await getMembers(orgNum);

      this.members = this.filterResponse(response);
      this.isCurrentAdmin = this.isCurrentUserAdmin();
    } catch (error) {
      console.log(error);
    }
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
#Add-user-form {
  margin: 0 3px 0 3px;
}
.search {
  width: 80%;
  border-style: solid;
}
</style>
