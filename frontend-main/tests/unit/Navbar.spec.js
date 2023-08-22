import { shallowMount, mount } from "@vue/test-utils";
import Navbar from "@/components/nav/Navbar";

const $store = {
  state: {
    user: {
      firstname: "Test",
      lastname: "Testing",
      image: null,
    },
  },
};

describe("Navbar", () => {
  it("Check if Navbar renders", () => {
    const wrapper = shallowMount(Navbar, {
      global: {
        mocks: {
          $store,
        },
      },
    });
    expect(wrapper.exists()).toBeTruthy();
  });
});
