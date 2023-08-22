import { shallowMount } from "@vue/test-utils";
import MemberList from "../../src/components/org/MemberList";

let members = [
  {
    firstName: "Kari",
    lastName: "Hansen",
    role: "EMPLOYEE",
  },
];

let wrapper;

describe("MemberList", () => {
  beforeEach(() => {
    wrapper = shallowMount(MemberList, {
      global: {
        stubs: ["FontAwesomeIcon"],
      },
      props: {
        members: members,
      },
    });
  });

  it("Check that MemberList renders", () => {
    expect(wrapper.exists()).toBeTruthy();
  });

  it("Check that members info renders", () => {
    expect(wrapper.html()).toContain("Kari");
    expect(wrapper.html()).toContain("Hansen");
  });

  it("Check that members role renders in norwegian", () => {
    expect(wrapper.html()).toContain("Ansatt");
  });

  it("Has deleteMember method", () => {
    const { vm } = wrapper;
    expect(typeof vm.deleteMember).toBe("function");
  });
});
