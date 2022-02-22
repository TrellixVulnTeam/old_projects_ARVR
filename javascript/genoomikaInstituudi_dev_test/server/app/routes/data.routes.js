
module.exports = app => {

    const data = require('../controllers/data.controller')

    let router = require('express').Router()

    router.get('/idCode/:idCode', data.getByIdCode) // get all matchin id codes
    
    router.post('/upload', data.upload)

    app.use('/api/data', router)
}