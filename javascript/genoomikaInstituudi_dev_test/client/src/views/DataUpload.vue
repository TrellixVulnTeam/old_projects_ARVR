<template>
  <div>
    <div v-if="message" class="text-white p-1 mb-2" :class="{ 'bg-danger': error && state === 'failed', 'bg-success': !error && state === 'success', 'bg-warning': error && state === 'success' }">
      <p class="p-2 m-0">{{ message }}</p>
    </div>
    <input type="file" name="dataFiles" class="btn" @change="saveFiles($event)" multiple>
    <button @click="processFiles()" class="btn btn-primary">Process file(s)</button>
    <hr>
    <div class="m-3" v-if="uploads.length">
      <p><b>Sent files</b></p>

      <div class="p-3 mb-2" :class="{
              'bg-dark text-white': file.state === 'idle', 
              'bg-success text-white': file.state === 'success',
              'bg-warning': file.state === 'uploading' || file.state === 'processing',
              'bg-danger': file.error || file.state === 'failed'
            }" v-for="(file, idx) of uploads" :key="idx">
        <p><b>File name:</b> {{ file.file.name }}</p>
        <p><b>Upload progress:</b> {{ file.uploadPercentage }}%</p>
        <p><b>Process status:</b> {{ file.state }}</p>
      </div>

    </div>
  </div>
</template>

<script>
import { useStore } from '../store'
import DataService from '../services/DataService'
import { computed, watchEffect } from '@vue/runtime-core'

export default {
  name: 'Upload',
  setup () {
    const store = useStore()

    // state
    const state = computed({
      get: () => store.getUploadState.value,
      set: store.updateUploadState
    })
    const loading = computed({
      get: () => store.getUploadLoading.value,
      set: store.updateUploadLoading
    })
    const message = computed({
      get: () => store.getUploadMessage.value,
      set: store.updateUploadMessage
    })
    const error = computed({
      get: () => store.getUploadError.value,
      set: store.updateUploadError
    })
    const selectedFiles = computed({
      get: () => store.getUploadSelectedFiles.value,
      set: store.updateUploadSelectedFiles
    })
    const uploads = computed({
      get: () => store.getUploadUploads.value,
      set: store.updateUploadUploads
    })

    // save files to state on input change
    const saveFiles = (e) => {
      let inputs = e.target.files
      if (!inputs.length) return
      selectedFiles.value = inputs
    }

    // send files to server
    const processFiles = () => {
      return new Promise((resolve, reject) => {
        try {
          const files = selectedFiles.value

          error.value = false
          message.value = null
          loading.value = true
          state.value = 'uploading'
          uploads.value = []

          if (files) {
            const api = new DataService()

            // for each file create FileUpload class that can track upload process
            for (const file of files) {

              const upload = new FileUpload(file)
              uploads.value.push(upload)

            }

            state.value = 'processing'

            // begin sending of files
            uploads.value.forEach(u => {
              u.state = 'uploading'
              api.upload(u)
            })

          }
        } catch (err) {
          reject(err)
        }

        // display message when all uploads are in finished states
        watchEffect(() => {
          const us = uploads.value
          if (us.length) {
            if (us.every(f => f.state === 'success' || f.state === 'failed'))
              if (us.some(f => f.state === 'failed')){
                error.value = true
                state.value = 'success'
                resolve('Some uploads failed')
              }
            else {
              state.value = 'success'
              resolve('Uploads are finished')
            }
          }
        })
      })
          .then(msg => {
            state.value = 'success'
            error.value = false
            message.value = msg
          })
          .catch(err => {
            state.value = 'failure'
            error.value = true
            message.value = err
          })
          .finally(() => loading.value = false)
    }

    return {
      processFiles,
      state,
      loading,
      message,
      error,
      selectedFiles,
      uploads,
      saveFiles
    }
  }
}

class FileUpload {
  constructor (file) {
    this.file = file
    this.uploadPercentage = 0
    this.error = false
    this.state = 'idle'
    this.message = null
  }
}

</script>
