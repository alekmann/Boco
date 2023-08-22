import { createStore } from "vuex";
import { fetchUser, refreshAccessToken } from "@/service/AuthenticationService";
import createPersistedState from "vuex-persistedstate";

export default createStore({
  state: {
    accessToken: undefined,
    user: {
      firstname: null,
      lastname: null,
      image: null,
    },
    selectedOrganization: null,
    loadSpinner: false,
    loadingMessage: "",
    rentalRequestsCount: 0,
  },
  plugins: [
    // Vuex plugin to presist the state.
    createPersistedState({
      paths: ["selectedOrganization"],
    }),
  ],
  mutations: {
    // Sets access token in state.
    SET_ACCESS_TOKEN(state, accessToken) {
      state.accessToken = accessToken;
    },
    SET_USER(state, user) {
      state.user = user;
    },
    SET_SELECTED_ORGANIZATION(state, organization) {
      state.selectedOrganization = organization;
    },
    // Shows loading wheel.
    SET_LOAD_SPINNER(state, loading) {
      state.loadSpinner = loading;
    },
    // Shows loading message.
    SET_LOADING_MESSAGE(state, message) {
      state.loadingMessage = message;
    },
    DECREMENT_REQUEST_COUNT(state) {
      state.rentalRequestsCount--;
    },
  },
  actions: {
    saveAccessToken({ commit }, accessToken) {
      commit("SET_ACCESS_TOKEN", accessToken);
    },
    showLoading({ commit }, message) {
      commit("SET_LOAD_SPINNER", true);
      commit("SET_LOADING_MESSAGE", message);
    },
    hideLoading({ commit }) {
      commit("SET_LOAD_SPINNER", false);
    },
    async fetchUser({ commit }) {
      const response = await fetchUser();
      commit("SET_USER", response.data);
    },
    setUser({ commit }, user) {
      commit("SET_USER", user);
    },
    async fetchAccessToken({ commit }) {
      const response = await refreshAccessToken();
      commit("SET_ACCESS_TOKEN", response.data.accessToken);
    },
    setSelectedOrganization({ commit }, organization) {
      commit("SET_SELECTED_ORGANIZATION", organization);
    },
    decrementRequestCounter({ commit }) {
      commit("DECREMENT_REQUEST_COUNT");
    },
    updateSelectedOrganization({ commit, state }) {
      // updates selected organization with the one from state.user.organizartions
      let orgs = state.user.organizations;
      for (let i = 0; i < orgs.length; i++) {
        if (orgs[i].orgNum === state.selectedOrganization.orgNum) {
          commit("SET_SELECTED_ORGANIZATION", orgs[i]);
          break;
        }
      }
    },
  },
  modules: {},
  getters: {
    getAccessToken(state) {
      return state.accessToken;
    },
    getUser(state) {
      return state.user;
    },
    getOrganization(state) {
      return state.selectedOrganization;
    },
    getUserPicture(state) {
      // Returns user profile picture if it exist, otherwise a placeholder
      if (state.user.profilePicture) {
        return state.user.profilePicture;
      } else {
        return require("@/assets/images/userIcon.jpeg");
      }
    },
    getOrgPicture(state) {
      // Returns organization profile picture if it exist, otherwise a placeholder
      if (state.selectedOrganization) {
        let organizations = state.user.organizations;
        if (organizations.length > 0) {
          for (let i = 0; i < organizations.length; i++) {
            if (organizations[i].orgNum === state.selectedOrganization.orgNum)
              if (organizations[i].profilePicture) {
                return organizations[i].profilePicture;
              } else {
                return require("@/assets/images/orgIcon.png");
              }
          }
        } else {
          return require("@/assets/images/orgIcon.png");
        }
      } else {
        return null;
      }
    },
    getOrgPictureSearch: (state) => (org) => {
      // Returns organization profile picture of selected organization if it exist, otherwise a placeholder
      let organizations = state.user.organizations;
      if (organizations.length > 0) {
        for (let i = 0; i < organizations.length; i++) {
          if (organizations[i].orgNum === org.orgNum)
            if (organizations[i].profilePicture) {
              return organizations[i].profilePicture;
            } else {
              return require("@/assets/images/orgIcon.png");
            }
        }
      } else {
        return require("@/assets/images/orgIcon.png");
      }
    },
  },
});
