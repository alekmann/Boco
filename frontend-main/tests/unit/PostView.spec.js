import { shallowMount } from "@vue/test-utils";
import PostView from "../../src/views/PostView";

describe("PostView", () => {
  it("Check that post info renders", () => {
    const wrapper = shallowMount(PostView, {
      global: {
        mocks: {
          $route: { params: { id: "" } },
        },
        stubs: ["VueEasyLightbox"],
      },
      data() {
        return {
          post: {
            title: "test title",
            description: "test description",
            user: {
              firstName: "Test",
              lastName: "Testesen",
              email: "test@test.no",
            },
          },
        };
      },
    });

    expect(wrapper.html()).toContain("test title");
    expect(wrapper.html()).toContain("test description");
    expect(wrapper.html()).toContain("Test");
    expect(wrapper.html()).toContain("Testesen");
    expect(wrapper.html()).toContain("test@test.no");
  });
});
