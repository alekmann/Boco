import { shallowMount } from "@vue/test-utils";
import Home from "@/views/Home.vue";

describe("Home", () => {
  it("check if home renders", () => {
    const wrapper = shallowMount(Home, {
      global: {
        stubs: ["FontAwesomeIcon"],
      },
      data() {
        return {
          search: {
            title: "",
            category: "",
            location: "",
          },
        };
      },
    });

    // see if the message renders
    expect(wrapper.find("#search").text()).toEqual("");

    // assert the error is rendered
    expect(wrapper.find("#search").exists()).toBeTruthy();
  });
});
