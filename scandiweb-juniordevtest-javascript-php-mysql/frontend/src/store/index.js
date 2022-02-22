import Vue from "vue";
import Vuex from "vuex";
import api from "../api";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    productsLoaded: false,
    products: [],
    saved: false,
    // selectedProducts: [],
  },
  mutations: {
    // toggleProduct(state, id) {
    //   if (state.selectedProducts.includes(id))
    //     state.selectedProducts = state.selectedProducts.filter((i) => i !== id);
    //   else state.selectedProducts.push(id);
    // },
    // setSelected(state, data) {
    //   if (data.state && !state.selectedProducts.includes(data.id)) {
    //     state.selectedProducts.push(data.id);
    //   } else if (!data.state && state.selectedProducts.includes(data.id)) {
    //     state.selectedProducts = state.selectedProducts.filter((id) => id !== data.id);
    //   }
    // },
    addProducts(state, data) {
      state.products = data;
    },
    notSaved(state) {
      state.saved = false;
    },
  },
  actions: {
    fetchProducts({ commit }) {
      if (!this.state.productsLoaded) {
        api
          .fetch()
          .then((data) => {
            commit("addProducts", data.data);
          })
          .finally(() => (this.state.productsLoaded = true));
      }
    },
    saveProduct({ dispatch, state }, data) {
      api.insert(data).then(() => {
        state.productsLoaded = false;
        dispatch("fetchProducts");
        state.saved = true;
      });
    },
    massDelete({ dispatch, state }) {
      let checked = document.getElementsByClassName("delete-checkbox");

      if (!checked) return;

      checked = Object.values(checked).map((check) => Number(check.id));

      let temp = state.products.filter((product) => {
        return checked.includes(product);
      });

      state.products = state.products.filter((product) => {
        return !checked.includes(product.id);
      });

      api.delete(checked).then(() => {
        state.productsLoaded = false;
        dispatch("fetchProducts");
      })
      .catch(() => {
        state.products.push(...temp);
        state.products.sort((a, b) => a.id - b.id);
      });
    },
  },
  modules: {},
});
