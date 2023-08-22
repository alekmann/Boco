import http from "@/service/http";

export const editName = (newName) => {
  return http.put("/me", { firstName: newName });
};

export const deleteUser = () => {
  return http.delete("/me");
};

export const changePassword = (passwordRequest) => {
  return http.patch("/me/password", passwordRequest);
};

export const getUserInfo = (id) => {
  return http.get("/users/" + id);
};

export const changePicture = (picture) => {
  return http.put("/me", { profilePicture: picture });
};
