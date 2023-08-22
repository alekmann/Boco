import { mount } from "@vue/test-utils";
import Register from "../../src/views/Register";
import TextInput from "@/components/form/TextInput";
import { register } from "@/service/AuthenticationService";
import { defineRule } from "vee-validate";
import { required } from "@vee-validate/rules";
defineRule("required", required);

jest.mock("@/service/AuthenticationService"); // mock the module
let wrapper;

describe("Register", () => {
  beforeEach(() => {
    wrapper = mount(Register, {
      global: {
        stubs: ["router-view", "router-link"],
      },
    });
  });

  it("Check that Register renders", () => {
    expect(wrapper.exists()).toBeTruthy();
  });

  it("Has onSubmit method", () => {
    const { vm } = wrapper;

    expect(typeof vm.onSubmit).toBe("function");
  });

  it("Check that Register title renders", () => {
    const registerTitle = "<h1>Registrer bruker</h1>";

    expect(wrapper.html()).toContain(registerTitle);
  });

  it("Render a TextInput component", () => {
    expect(wrapper.findComponent(TextInput).exists()).toBe(true);
  });

  it("Renders many children (TextInput components)", () => {
    expect(wrapper.findAllComponents(TextInput).length).toBe(5);
  });

  it("Calls register method once, check the call and response", async () => {
    // mock register request, and make the response status 200
    const registerResponseStatus = 200;

    register.mockImplementationOnce(() =>
      Promise.resolve({ status: registerResponseStatus })
    );

    // do the call
    const registerRequest = {
      email: "Kurl@mail.com",
      firstname: "Kurl",
      lastname: "Olsen",
      password: "1234",
    };
    const registerResponse = await register(registerRequest);

    // check the response
    expect(registerResponse.status).toEqual(registerResponseStatus);

    // check how many times it has been called
    expect(register).toHaveBeenCalledTimes(1); // check that call happened once
  });
});
