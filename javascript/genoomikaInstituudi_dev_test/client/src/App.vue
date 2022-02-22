<template>
  <div id="app">

    <nav class="navbar navbar-expand navbar-dark bg-dark">
      <div class="navbar-nav mr-auto">
        <li class="nav-item">
          <router-link to="/upload" class="nav-link">Upload</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/search" class="nav-link">Search</router-link>
        </li>
      </div>
    </nav>

    <div class="container-fluid p-2">
      <router-view/>
    </div>

  </div>
</template>

<script>
import { initStore } from './store'

export default {
  setup() {
    initStore()
  },
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>
