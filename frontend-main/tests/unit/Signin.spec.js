import { mount } from "@vue/test-utils";
import SignIn from "../../src/views/SignIn";
import TextInput from "@/components/form/TextInput";
import { login } from "@/service/AuthenticationService";
import { defineRule } from "vee-validate";
import { required } from "@vee-validate/rules";
defineRule("required", required);

jest.mock("@/service/AuthenticationService"); // mock the module
let wrapper;

describe("SignIn", () => {
  beforeEach(() => {
    wrapper = mount(SignIn, {
      global: {
        stubs: ["router-view", "router-link"],
      },
    });
  });

  it("Check that SignIn renders", () => {
    expect(wrapper.exists()).toBeTruthy();
  });

  it("Has onSubmit method", () => {
    const { vm } = wrapper;

    expect(typeof vm.onSubmit).toBe("function");
  });

  it("Check that login title renders", () => {
    const loginTitle = "<h1>Pålogging</h1>";

    expect(wrapper.html()).toContain(loginTitle);
  });

  it("Render a TextInput component", () => {
    expect(wrapper.findComponent(TextInput).exists()).toBe(true);
  });

  it("Renders many children (TextInput components)", () => {
    expect(wrapper.findAllComponents(TextInput).length).toBe(2);
  });

  it("Calls login method once, check the call and response", async () => {
    //mock login request, and make the response status 200
    const loginResponseStatus = 200;

    login.mockImplementationOnce(() =>
      Promise.resolve({ status: loginResponseStatus })
    );

    // do the call
    const loginRequest = { email: "Kurl@mail.com", password: "1234" };
    const loginResponse = await login(loginRequest);

    // check the response
    expect(loginResponse.status).toEqual(loginResponseStatus);

    // check how many times it has been called
    expect(login).toHaveBeenCalledTimes(1); // check that call happened once
  });
});
