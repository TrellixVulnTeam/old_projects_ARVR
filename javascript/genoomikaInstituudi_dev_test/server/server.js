const express = require('express')
const cors = require('cors')

const app = express()
const PORT = 5000

app.use(cors())
app.use(express.json())
app.use(express.urlencoded())

const db = require('./app/models')

// able to reset db after restart
db.sequelize.sync()

// force re-create
// db.sequelize.sync({ force: true })
// .then(() => console.log('Drop and re-sync db.'))

require('./app/routes/data.routes')(app)

app.listen(PORT, () => {
    console.log(`Listening from port ${PORT}`)
})