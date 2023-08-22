import http from "./http";

export const getAllCategories = () => {
  return http.get("/categories");
};
