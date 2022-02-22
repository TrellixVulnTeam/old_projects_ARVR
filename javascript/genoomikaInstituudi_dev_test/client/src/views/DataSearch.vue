<template>
  <div class="container-fluid p-4">

    <div class="d-flex flex-row justify-content-between mb-3">
      
      <div>
        <div v-if="result.length">
          <p>{{ result.length }} matches</p>
          <p>Showing results between {{ 100 * (currentDisplay - 1) + 1 }}-{{ 100 * currentDisplay > result.length ? result.length : 100 * currentDisplay }}</p>
        </div>
        <p v-else>Make a search</p>
      </div>
      <div>
        <button :disabled="!result.length || currentDisplay === 1" :class="{ 'btn-outline-dark': !result.length || currentDisplay === 1, 'btn-dark': result.length && currentDisplay > 1 }" class="m-2 btn-sm" @click="previousPage()" type="button">Previous page</button>
        <button :disabled="!result.length || 100 * currentDisplay > result.length" :class="{ 'btn-outline-dark': !result.length || 100 * currentDisplay > result.length, 'btn-dark': result.length && 100 * currentDisplay < result.length }" class="m-2 btn-sm" @click="nextPage()" type="button">Next page</button>
      </div>

    </div>

    <div class="input-group mb-3 m-auto w-50">
      <input type="text" class="form-control" v-model="statement" placeholder="Id code">
      <div class="input-group-append">
        <button @click="searchByStatement()" class="btn btn-primary">Search</button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-hover table-bordered table-sm">
        <thead>
          <tr>
            <th @click="sortTable('id')" scope="col">#</th>
            <th @click="sortTable('code')" scope="col">Code</th>
            <th @click="sortTable('department')" scope="col">Department</th>
            <th @click="sortTable('visitTime')" scope="col">Visit Time</th>
            <th @click="sortTable('firstName')" scope="col">First</th>
            <th @click="sortTable('lastName')" scope="col">Last</th>
            <th @click="sortTable('email')" scope="col">Email</th>
            <th @click="sortTable('idCode')" scope="col">Id</th>
            <th @click="sortTable('birthDate')" scope="col">Birth Date</th>
            <th @click="sortTable('gender')" scope="col">Gender</th>
            <th @click="sortTable('age')" scope="col">Age</th>
          </tr>
        </thead>
        <tbody  v-if="!loading && result.length">
          <DataRow v-for="(data, index) in slicedResults" :key="index" :data="data" :idx="(100 * (currentDisplay - 1)) + index"/>
        </tbody>
      </table>
    </div>

    <p v-if="!loading && !result.length">No data to show</p>
    <p v-else-if="loading">Fetching data...</p> 

  </div>
</template>

<script>
import { computed } from 'vue'
import DataRow from '../components/DataRow'
import { useStore } from '../store'
import DataService from '../services/DataService'

export default {
  name: 'Data Search',
  components: {
    DataRow
  },
  setup () {
    const store = useStore()

    // state
    const state = computed({
      get: () => store.getSearchState.value,
      set: store.updateSearchState
    })
    const loading = computed({
      get: () => store.getSearchLoading.value,
      set: store.updateSearchLoading
    })
    const message = computed({
      get: () => store.getSearchMessage.value,
      set: store.updateSearchMessage
    })
    const error = computed({
      get: () => store.getSearchError.value,
      set: store.updateSearchError
    })
    const result = computed({
      get: () => store.getSearchResult.value,
      set: store.updateSearchResult
    })
    const statement = computed({
      get: () => store.getSearchStatement.value,
      set: store.updateSearchStatement
    })
    const currentSort = computed({
      get: () => store.getSearchCurrentSort.value,
      set: store.updateSearchCurrentSort
    })
    const currentSortDir = computed({
      get: () => store.getSearchCurrentSortDir.value,
      set: store.updateSearchCurrentSortDir
    })
    const currentDisplay = computed({
      get: () => store.getSearchCurrentDisplay.value,
      set: store.updateSearchCurrentDisplay
    })

    // create a query for the given id code
    const searchByStatement = () => {
      return new Promise((resolve, reject) => {
        const api = new DataService()

        error.value = false
        message.value = null
        result.value = []

        if (!statement.value) reject({
            message: 'enter a id code',
            state: 'idle'
          })

        loading.value = true

        api.getByIdCode(statement.value)
            .then(res => {
              resolve({
                r_message: res.data.message,
                r_result: res.data.result,
                r_state: 'success'
              })
            })
            .catch(res => {
              reject({
                r_message: res.data.message,
                r_state: 'failed',
              })
            })
      })
          .then(res => {
            const { r_message, r_state, r_result } = res
            result.value = r_result
            message.value = r_message
            error.value = false
            state.value = r_state
          })
          .catch(res => {
            const { r_message, r_state } = res
            message.value = r_message
            error.value = true
            state.value = r_state
          })
          .finally(() => loading.value = false )
    }

    // clicks on table header change table sort type
    const sortTable = (col) => {
      if (currentSort.value === col) {
        currentSortDir.value = currentSortDir.value === 'asc' ? 'desc' : 'asc'
      }
      else {
        currentSort.value = col
      }

      currentDisplay.value = 1
    }

    // sort data when sort type is changed
    const sortedResults = computed(() => {
      let arr = result.value
      return arr.sort((a, b) => {
        let mod = 1
        const a_val = a[currentSort.value]
        const b_val = b[currentSort.value]
        if (currentSortDir.value === 'desc') mod = -1
        if (currentSort.value === 'age') {
          return (Number(a_val) - Number(b_val)) * mod
        }
        else {
          if (a_val < b_val) return -1 * mod
          if (a_val > b_val) return 1 * mod
        }
        return 0
      })
    })

    // slice received data into chunks with length 100 
    const slicedResults = computed(() => {
      const CurrentPage = currentDisplay.value
      const lenOfResult = result.value.length

      const start = (CurrentPage - 1) * 100
      const end = lenOfResult < CurrentPage * 100 ? lenOfResult : CurrentPage * 100

      return sortedResults.value.slice(start, end)
    })

    const previousPage = () => {
      currentDisplay.value--
    }

    const nextPage = () => {
      currentDisplay.value++
    }

    return {
      state, loading, message, error, result, statement, searchByStatement, sortTable, sortedResults, currentDisplay,
      previousPage, nextPage, slicedResults
    }
  }
}
</script>
