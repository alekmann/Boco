<template>
  <table>
    <tr>
      <th>Fornavn</th>
      <th>Etternavn</th>
      <th>Rolle</th>
      <th v-show="isCurrentAdmin"></th>
    </tr>
    <tr v-for="member in members" :key="member">
      <td>{{ member.firstName }}</td>
      <td>{{ member.lastName }}</td>
      <td>{{ member.role === "ADMIN" ? "Admin" : "Ansatt" }}</td>
      <td v-show="isCurrentAdmin">
        <font-awesome-icon
          @click="deleteMember(member)"
          icon="trash-can"
          class="icon"
          fixed-width
        />
      </td>
    </tr>
  </table>
</template>

<script>
import { deleteMember } from "@/service/OrganizationService";

export default {
  components: {},
  props: {
    members: Array,
    isCurrentAdmin: Boolean,
  },
  methods: {
    //Deletes member from organisation.
    async deleteMember(member) {
      /**
       * Check how many members has role ADMIN.
       * The organisation must have at least one admin, deleting the last admin is not allowed.
       * */
      let adminsCount = this.members.filter(
        (user) => user.role === "ADMIN"
      ).length;
      if (adminsCount === 1 && member.role === "ADMIN") {
        // there is just one admin, deleting is not allowed
        this.$notify({
          type: "error",
          title: "Feilet",
          text: `Organisasjonen må ha minst en admin`,
        });
      } else {
        // delete, just admins can delete
        if (this.isCurrentAdmin) {
          try {
            let orgNum = this.$store.getters.getOrganization.orgNum;
            await deleteMember(orgNum, member.id);
            this.$router.go(0); // refresh the page
            this.$notify({
              type: "success",
              title: "Vellykket",
              text: `${member.firstName} ${member.lastName} ble slettet`,
            });
          } catch (error) {
            console.log(error);
          }
        }
      }
    },
  },
};
</script>

<style>
.icon {
  font-size: 28px;
  transition: 0.2s;
  cursor: pointer;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin: 25px 0;
  font-size: 1em;
  font-family: arial, sans-serif;
  min-width: 400px;
}

th,
td {
  text-align: left;
  padding: 8px;
  border: 1px solid #dddddd;
}

th {
  background-color: #605f5f;
  color: #ffffff;
}

tr:nth-child(odd) {
  background-color: #dddddd;
}
</style>
