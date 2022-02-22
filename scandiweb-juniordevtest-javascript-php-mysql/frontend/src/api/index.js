import axios from "axios";

axios.defaults.baseURL = "https://scandiweb-test-server.herokuapp.com";
const URL = "/";

export default {
  fetch: () => {
    return axios.get(URL).then((response) => response.data);
  },
  delete: (data) => {
    return axios.delete(URL, {
      data: {
        ids: data,
      },
    });
  },
  insert: (params) => {
    return axios.post(URL, params);
  },
};
