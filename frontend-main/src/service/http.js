import axios from "axios";
import jwtDecode from "jwt-decode";
import store from "@/store";

/**
 * This class sets up an instance of axios with a certain set of configurations that can be used around the
 * application
 *
 * @returns instance of axios with wanted config
 */
const fetchClient = () => {
  const options = {
    baseURL: "http://localhost:8000",
    headers: {
      "Content-type": "application/json",
    },
  };

  let instance = axios.create(options);

  instance.interceptors.request.use(
    async (config) => {
      store.dispatch("showLoading", "Loading");
      let token = store.getters.getAccessToken;
      // If the token is expired, a new one is fetched
      if (!isTokenValidOrUndefined(token)) {
        try {
          let instance2 = axios.create(options);
          const response = await instance2.post("/token", "", {
            withCredentials: true,
          });
          token = response.data.accessToken;
          store.dispatch("saveAccessToken", token);
        } catch (err) {
          console.log(err);
        }
      }

      config.headers.Authorization = token ? `Bearer ${token}` : "";
      return config;
    },
    function () {
      store.dispatch("hideLoading");
    }
  );

  instance.interceptors.response.use(
    function (response) {
      store.dispatch("hideLoading");
      return response;
    },
    function (error) {
      // Any status codes that falls outside the range of 2xx cause this function to trigger
      // Do something with response error
      store.dispatch("hideLoading");
      return Promise.reject(error);
    }
  );

  return instance;
};

/**
 * Checks whether the current access is still valid
 *
 * @returns true if the token is valid or undefined, otherwise false
 */
const isTokenValidOrUndefined = (token) => {
  if (!token) {
    return true;
  }
  try {
    const decoded = jwtDecode(token);
    return Date.now() < decoded.exp * 1000;
  } catch {
    return false;
  }
};

export default fetchClient();
