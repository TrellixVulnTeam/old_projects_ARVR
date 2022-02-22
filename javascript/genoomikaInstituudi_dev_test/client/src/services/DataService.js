import api from '../api'

class DataService {

  getByIdCode (statement) {
    return api.get(`/data/idCode/${statement}`)
  }

  async upload (file) {
    return api.post('/data/upload', file.file, {
      headers: {
        'Content-type': 'multipart/form-data'
      },
      onUploadProgress: function (progressEvent) {
        file.uploadPercentage = parseInt( Math.round( ( progressEvent.loaded / progressEvent.total ) * 100 ) )
        if (file.uploadPercentage === 100) file.state = 'processing'
      }.bind(file)
    })
        .then(() => {
          file.state = 'success'
        })
        .catch(() => {
          file.state = 'failed'
        })
  }
}

export default DataService
