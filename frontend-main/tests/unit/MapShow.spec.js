import { shallowMount } from "@vue/test-utils";
import MapShow from "../../src/components/map/MapShow.vue";

describe("MapShow", () => {
  it("Check map width is correct", () => {
    const wrapper = shallowMount(MapShow, {
      data() {
        return {
          location: {
            address: "gate 2",
            postCode: "1234",
            country: "Norge",
            city: "Oslo",
          },
        };
      },
    });

    expect(wrapper.html()).toContain("gate 2");
    expect(wrapper.html()).toContain("1234");
    expect(wrapper.html()).toContain("Norge");
    expect(wrapper.html()).toContain("Oslo");
  });
});
