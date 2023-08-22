import http from "./http";

export const login = (user) => {
  return http.post("/login", user, { withCredentials: true });
};

export const register = (user) => {
  return http.post("/register", user);
};

// Collects new access token.
export const refreshAccessToken = () => {
  return http.post("/token", "", { withCredentials: true });
};

export const logout = () => {
  return http.post("/logout", "", { withCredentials: true });
};

export const fetchUser = () => {
  return http.get("/me");
};

// Activates user when email is activated.
export const activateUser = (userId) => {
  return http.post(`/activate/${userId}`);
};
