<template>
  <div>
    <div id="header" class="d-flex flex-row justify-content-between m-5 mb-3">
      <div>
        <span class="text-nowrap fs-3">Product List</span>
      </div>
      <div class="btn-group" role="group">
        <router-link to="/addproduct" class="btn btn-outline-primary"
          >ADD</router-link
        >
        <button
          id="delete-product-btn"
          type="button"
          class="btn btn-outline-primary"
          @click="$store.dispatch('massDelete')"
        >
          MASS DELETE
        </button>
      </div>
    </div>

    <hr />

    <div class="min-vh-100">
      <div
        v-if="$store.state.products"
        class="d-flex flex-wrap justify-content-center mx-5 my-3 gap-3">
        <Product
          v-for="product in $store.state.products"
          :key="product.id"
          :details="product"
        >
          <div class="custom-checkbox text-start p-1">
            <input
              type="checkbox"
              v-bind:id="product.id"
              class="delete-checkbox form-check-input m-1"
            />
          </div>
        </Product>
      </div>
      <div v-else>No Products</div>
    </div>

    <hr />

    <div id="footer" class="m-3">
      <span class="fs-5">Scandiweb Test Assignment</span>
    </div>
  </div>
</template>

<script>
import Product from "../components/Product.vue";

export default {
  name: "Products",
  async mounted() {
    this.$store.dispatch("fetchProducts");
  },
  components: { Product },
};
</script>
