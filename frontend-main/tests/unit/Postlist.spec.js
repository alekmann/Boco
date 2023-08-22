import { shallowMount } from "@vue/test-utils";
import PostList from "@/components/post/PostList.vue";
import PostCard from "@/components/post/PostCard.vue";

describe("PostList", () => {
  it("Check if card shows up", () => {
    const wrapper = shallowMount(PostList, {
      props: {
        posts: {
          content: [
            {
              title: "post1",
            },
          ],
        },
      },
    });
    // assert the error is rendered
    expect(wrapper.findComponent(PostCard).exists()).toBeTruthy();
  });

  it("Check that no cards is shown", () => {
    const wrapper = shallowMount(PostList, {
      props: {
        posts: {
          content: [],
        },
      },
    });

    expect(wrapper.findComponent(PostCard).exists()).toBeFalsy();
  });

  it("Check that right count of cards renders", () => {
    const wrapper = shallowMount(PostList, {
      global: {
        stubs: ["PostCard"],
      },
      props: {
        posts: {
          content: [
            {
              title: "post1",
            },
            {
              title: "post2",
            },
          ],
        },
      },
    });
    //assert it has 2 cards
    expect(wrapper.findAllComponents(PostCard).length).toBe(2);
  });
});
