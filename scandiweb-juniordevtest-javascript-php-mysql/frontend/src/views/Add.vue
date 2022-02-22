<template>
  <div>
    <div id="header" class="d-flex flex-row justify-content-between m-5 mb-3">
      <div>
        <span class="text-nowrap fs-3">Product Add</span>
      </div>
      <div class="btn-group" role="group">
        <button
          type="submit"
          form="product_form"
          class="btn btn-outline-primary"
        >
          Save
        </button>
        <router-link to="/" class="btn btn-outline-primary">Cancel</router-link>
      </div>
    </div>

    <hr />

    <form id="product_form" class="w-50 m-auto min-vh-100" @submit="addProduct">
      <div
        v-if="saved && sku === ''"
        class="alert alert-danger text-start"
        role="alert"
      >
        Please, submit required data
      </div>
      <div class="input-group mb-3">
        <span class="input-group-text">SKU</span>
        <input
          v-model="sku"
          type="text"
          class="form-control"
          name="sku"
          id="sku"
        />
      </div>

      <div
        v-if="saved && name === ''"
        class="alert alert-danger text-start"
        role="alert"
      >
        Please, submit required data
      </div>
      <div class="input-group mb-3">
        <span class="input-group-text">Name</span>
        <input
          v-model="name"
          type="text"
          class="form-control"
          name="name"
          id="name"
        />
      </div>

      <div
        v-if="saved && price === null"
        class="alert alert-danger text-start"
        role="alert"
      >
        Please, submit required data
      </div>
      <div
        v-if="saved && price !== null && typeof price !== 'number' && price < 1"
        class="alert alert-warning text-start"
        role="alert"
      >
        Please, provide the data of indicated type
      </div>
      <div class="input-group mb-3">
        <span class="input-group-text">Price ($)</span>
        <input
          v-model="price"
          type="number"
          class="form-control"
          name="price"
          id="price"
          min="1"
        />
      </div>

      <div
        v-if="saved && productType === ''"
        class="alert alert-danger text-start"
        role="alert"
      >
        Select a product type to finish the form
      </div>
      <div class="input-group mb-3">
        <label for="typeSelection" class="input-group-text"
          >Type Switcher</label
        >
        <select
          name="productType"
          id="productType"
          class="form-select"
          v-model="productType"
        >
          <option disabled value="" selected>Select a type...</option>
          <option value="dvd">DVD</option>
          <option value="book">Book</option>
          <option value="furniture">Furniture</option>
        </select>
      </div>

      <div v-if="productType === 'dvd'" id="DVD">
        <p>Please, provide size</p>
        <div
          v-if="saved && dvd === null"
          class="alert alert-danger text-start"
          role="alert"
        >
          Please, submit required data
        </div>
        <div
          v-if="
            saved &&
            productType === 'dvd' &&
            dvd !== null &&
            typeof dvd !== 'number' &&
            dvd < 1
          "
          class="alert alert-warning text-start"
          role="alert"
        >
          DVD size needs to be a (positive) number
        </div>
        <div class="input-group mb-3">
          <label for="dvdSize" class="input-group-text">Size (MB)</label>
          <input
            v-model="dvd"
            type="number"
            name="dvdSize"
            id="size"
            class="form-control"
            min="1"
          />
        </div>
      </div>

      <div v-else-if="productType === 'furniture'" id="Furniture">
        <p>Please, provide dimensions</p>
        <div
          v-if="
            saved &&
            (furnitureHeight === null ||
              furnitureWidth === null ||
              furnitureLength === null)
          "
          class="alert alert-danger text-start"
          role="alert"
        >
          Please, submit required data
          <ul>
            <li v-if="furnitureHeight === null">height</li>
            <li v-if="furnitureWidth === null">width</li>
            <li v-if="furnitureLength === null">length</li>
          </ul>
        </div>
        <div
          v-if="
            saved &&
            productType === 'furniture' &&
            ((furnitureHeight !== null && furnitureHeight < 1) ||
              (furnitureWidth !== null && furnitureWidth < 1) ||
              (furnitureLength !== null && furnitureLength < 1))
          "
          class="alert alert-warning text-start"
          role="alert"
        >
          The following dimensions are invalid:
          <ul>
            <li v-if="furnitureHeight !== null && furnitureHeight < 1">
              height
            </li>
            <li v-if="furnitureWidth !== null && furnitureWidth < 1">width</li>
            <li v-if="furnitureLength !== null && furnitureLength < 1">
              length
            </li>
          </ul>
        </div>
        <div class="input-group mb-3">
          <label for="furnitureHeight" class="input-group-text"
            >Height (CM)</label
          >
          <input
            type="number"
            name="furnitureHeight"
            id="height"
            class="form-control"
            v-model="furnitureHeight"
          />
        </div>
        <div class="input-group mb-3">
          <label for="furnitureWidth" class="input-group-text"
            >Width (CM)</label
          >
          <input
            type="number"
            name="furnitureWidth"
            id="width"
            class="form-control"
            v-model="furnitureWidth"
          />
        </div>
        <div class="input-group mb-3">
          <label for="furnitureLength" class="input-group-text"
            >Length (CM)</label
          >
          <input
            type="number"
            name="furnitureLength"
            id="length"
            class="form-control"
            v-model="furnitureLength"
          />
        </div>
      </div>

      <div v-else-if="productType === 'book'" id="Book">
        <p>Please, provide weight</p>
        <div
          v-if="saved && book === null"
          class="alert alert-danger text-start"
          role="alert"
        >
          Please, submit required data
        </div>
        <div
          v-if="
            saved &&
            productType === 'book' &&
            book !== null &&
            typeof book !== 'string' &&
            book < 1
          "
          class="alert alert-danger text-start"
          role="alert"
        >
          Book weight needs to be a (positive) number
        </div>
        <div class="input-group mb-3">
          <label for="bookWeight" class="input-group-text">Weight (KG)</label>
          <input
            type="number"
            name="bookWeight"
            id="weight"
            class="form-control"
            v-model="book"
          />
        </div>
      </div>
    </form>

    <hr />

    <div id="footer" class="m-3">
      <span class="fs-5">Scandiweb Test Assignment</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "Add",
  data: () => {
    return {
      saved: false,
      sku: "",
      name: "",
      price: null,
      dvd: null,
      book: null,
      furnitureHeight: null,
      furnitureWidth: null,
      furnitureLength: null,
      productType: "",
    };
  },
  computed: {
    itemSaved: function () {
      console.log(this.$store.state.saved);
      return this.$store.state.saved;
    },
  },
  watch: {
    itemSaved() {
      this.$router.push("/");
      this.$store.commit("notSaved");
    },
  },
  methods: {
    addProduct: function (e) {
      e.preventDefault();

      this.saved = true;

      if (
        this.sku === "" ||
        this.name === "" ||
        this.price < 1 ||
        (this.productType === "dvd" && (!this.dvd || this.dvd < 1)) ||
        (this.productType === "book" && (!this.book || this.book < 1)) ||
        (this.productType === "furniture" &&
          (!this.furnitureHeight ||
            this.furnitureHeight < 1 ||
            !this.furnitureWidth ||
            this.furnitureWidth < 1 ||
            !this.furnitureLength ||
            this.furnitureLength < 1))
      ) {
        return;
      }

      let data = {
        sku: this.sku,
        name: this.name,
        price: this.price,
        type: this.productType,
      };

      if (this.productType === "dvd") {
        data = Object.assign(data, {
          size: this.dvd,
        });
      } else if (this.productType === "book") {
        data = Object.assign(data, {
          weight: this.book,
        });
      } else {
        data = Object.assign(data, {
          height: this.furnitureHeight,
          width: this.furnitureWidth,
          length: this.furnitureLength,
        });
      }

      this.$store.dispatch("saveProduct", data);
    },
  },
};
</script>
