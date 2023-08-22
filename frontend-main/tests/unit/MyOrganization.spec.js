import { shallowMount, mount } from "@vue/test-utils";
import MyOrganization from "@/views/MyOrganization";
import { createStore } from "vuex";

// Creates a store that does has a selected organization
const orgStore = createStore({
  state: {
    user: {
      firstName: "ola",
      lastName: "nordmann",
      email: "ola@nordmann.no",
      organizations: [],
    },
    selectedOrganization: {
      orgName: "norgesbedrift AS",
      orgNum: 123456789,
    },
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
    getOrganization(state) {
      return state.selectedOrganization;
    },
  },
});

describe("Profile", () => {
  it("should render the component", function () {
    const wrapper = shallowMount(MyOrganization, {
      global: {
        plugins: [orgStore],
        stubs: ["router-view", "router-link"],
      },
    });

    expect(wrapper.exists()).toBeTruthy();
    expect(wrapper.find("#profile-grid").exists()).toBeTruthy();
  });

  it("should only display org name", function () {
    const wrapper = shallowMount(MyOrganization, {
      global: {
        plugins: [orgStore],
        stubs: ["router-view", "router-link"],
      },
    });

    // Because organization is selected, only org info should be displayed
    expect(wrapper.text()).toMatch("norgesbedrift AS");
    expect(wrapper.text()).toMatch("123456789");
    expect(wrapper.text()).not.toMatch("ola nordmann");
    expect(wrapper.text()).not.toMatch("ola@nordmann.no");
  });

  it("should display org image", function () {
    const wrapper = shallowMount(MyOrganization, {
      global: {
        plugins: [orgStore],
        stubs: ["router-view", "router-link"],
      },
    });

    // Because organization is selected, only org image should be displayed
    expect(
      wrapper.find('[data-testid="profile-picture"]').attributes().src
    ).toBe("orgimg");
  });

  it("should only display org navigation", function () {
    const wrapper = mount(MyOrganization, {
      global: {
        plugins: [orgStore],
        stubs: ["router-view", "router-link"],
      },
    });

    // Because organization is selected, only org navigation should be displayed
    expect(wrapper.html()).toMatch("administrateorg");
    expect(wrapper.html()).toMatch("members");
    expect(wrapper.html()).not.toMatch("administrateuser");
    expect(wrapper.html()).not.toMatch("organization");
  });
});
