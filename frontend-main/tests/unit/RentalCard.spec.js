import { shallowMount } from "@vue/test-utils";
import RentalCard from "../../src/components/rental/RentalCard";

describe("RentalCard", () => {
  it("Check that rental info renders", () => {
    const wrapper = shallowMount(RentalCard, {
      props: {
        rental: {
          post: {
            postId: 1,
            title: "test title",
          },
          owner: {
            firstName: "owner firstName",
            lastName: "owner lastName",
          },
          totalPrice: 100,
          customer: {
            firstName: "Test",
            lastName: "Testesen",
            email: "test@test.no",
          },
        },
      },
    });

    expect(wrapper.html()).toContain("test title");
    expect(wrapper.html()).toContain("100 kr");
    expect(wrapper.html()).toContain("owner firstName");
    expect(wrapper.html()).toContain("owner lastName");
  });
});
