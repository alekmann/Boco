import { shallowMount, mount } from "@vue/test-utils";
import Organization from "@/components/profile/Organization";

jest.mock("@/service/OrganizationService.js");
const $store = {
  state: {
    user: {
      organizations: [],
    },
  },
  getters: {
    getUser(state) {
      return state.user;
    },
  },
};

describe("Organization", () => {
  it("Check that organization renders", () => {
    const wrapper = shallowMount(Organization, {
      global: {
        mocks: {
          $store,
        },
      },
    });

    expect(wrapper.exists()).toBeTruthy();
  });
});
