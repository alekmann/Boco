import { shallowMount, mount } from "@vue/test-utils";
import MyProfile from "@/views/MyProfile";
import { createStore } from "vuex";

// Creates a store that does not have a selected organization
const userStore = createStore({
  state: {
    user: {
      firstName: "ola",
      lastName: "nordmann",
      email: "ola@nordmann.no",
      organizations: [],
    },
    selectedOrganization: null,
  },
  getters: {
    getUser(state) {
      return state.user;
    },
    getUserPicture() {
      return "userimg";
    },
    getOrgPicture() {
      return "orgimg";
    },
  },
});

describe("Profile", () => {
  it("should render the component", function () {
    const wrapper = shallowMount(MyProfile, {
      global: {
        plugins: [userStore],
        stubs: ["router-view", "router-link"],
      },
    });

    expect(wrapper.exists()).toBeTruthy();
    expect(wrapper.find("#profile-grid").exists()).toBeTruthy();
  });

  it("should only display user name", function () {
    const wrapper = shallowMount(MyProfile, {
      global: {
        plugins: [userStore],
        stubs: ["router-view", "router-link"],
      },
    });

    // Because organization is not selected, only user info should be displayed
    expect(wrapper.text()).toMatch("ola nordmann");
    expect(wrapper.text()).toMatch("ola@nordmann.no");
    expect(wrapper.text()).not.toMatch("norgesbedrift AS");
    expect(wrapper.text()).not.toMatch("123456789");
  });

  it("should display user image", function () {
    const wrapper = shallowMount(MyProfile, {
      global: {
        plugins: [userStore],
        stubs: ["router-view", "router-link"],
      },
    });

    // Because organization is not selected, only user image should be displayed
    expect(
      wrapper.find('[data-testid="profile-picture"]').attributes().src
    ).toBe("userimg");
  });

  it("should only display user navigation", function () {
    const wrapper = mount(MyProfile, {
      global: {
        plugins: [userStore],
        stubs: ["router-view", "router-link"],
      },
    });

    // Because organization is not selected, only user navigation should be displayed
    expect(wrapper.html()).toMatch("administrateuser");
    expect(wrapper.html()).toMatch("organization");
    expect(wrapper.html()).not.toMatch("administrateorg");
    expect(wrapper.html()).not.toMatch("members");
  });
});
