<template>
  <div class="card shadow" style="width: 24rem">
    <slot></slot>
    <div class="mb-2 h4 d-flex flex-column gap-3">
      <div>{{ details.sku }}</div>
      <div>{{ details.name }}</div>
      <div>{{ details.price }} $</div>
      <div>{{ computedAttribute }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Product",
  props: {
    details: {
      type: Object,
      required: true,
    },
  },
  data: () => {
    return {};
  },
  computed: {
    computedAttribute: function () {
      let attr = this.details.attribute;
      let value = "";
      switch (this.details.type) {
        case "dvd":
          value = `Size: ${attr} MB`;
          break;
        case "book":
          value = `Weight: ${attr}KG`;
          break;
        case "furniture":
          value = `Dismension: ${attr}`;
      }

      return value;
    },
  },
};
</script>
