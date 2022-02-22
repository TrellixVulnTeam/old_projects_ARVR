<template>
    <tr>
      <th scope="row">{{ index + 1 }}</th>
      <td>{{ d.code }}</td>
      <td>{{ d.department }}</td>
      <td>{{ d.visitTime }}</td>
      <td>{{ d.firstName }}</td>
      <td>{{ d.lastName }}</td>
      <td>{{ d.email }}</td>
      <td>{{ d.idCode }}</td>
      <td>{{ d.birthDate }}</td>
      <td>{{ d.gender }}</td>
      <td>{{ d.age }}</td>
    </tr>
</template>

<script>
import { toRefs } from 'vue'

export default {
  props: {
    data: {
      id: String,
      code: String,
      department: String,
      visitTime: String,
      firstName: String,
      lastName: String,
      email: String,
      idCode: String,
      birthDate: String,
      gender: String,
      age: String
    },
    idx: Number
  },
  setup (props) {
    const { data, idx } = toRefs(props)

    return {
      d: data,
      index: idx
    }
  }
}
</script>
