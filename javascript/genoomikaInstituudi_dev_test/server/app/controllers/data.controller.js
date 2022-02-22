const db = require('../models')
const fs = require('fs')
const readline = require('readline')
const path = require('path')
const { resolve } = require('path')
const Data = db.data
const Op = db.Sequelize.Op

let uploadCount = 0

// get any entitie where id starts with given param
const getByIdCode = async (req, res) => {
    return new Promise((resolve, reject) => {
        const { idCode } = req.params

        // erro when no parameter was found
        if (!idCode) reject({
            message: 'No id code was provided',
            status: 400
        })

        // query database for entities with id code starting with given param
        Data.findAll({
            where: {
                idCode: {
                    [Op.startsWith]: idCode
                }
            }
        })
                .then(result => resolve({
                    result,
                    message: 'Data retrieval successful'
                }))
                .catch(result => reject({
                    status: 500,
                    message: 'Error occured while retrieving data by idCode'
                }))

    })
    .then(data => {
        const { result, message } = data
        res.status(200).json({
            message,
            result: result
        })
    })
    .catch(data => {
        const { status, message } = data
        res.status(status || 500).json({
            message
        })
    })
}

// stream download a file then process and add data to db
const upload = (req, res) => {
    const uploadsDir = path.join(__dirname, '../uploads')

    if (!fs.existsSync(uploadsDir)) fs.mkdirSync(uploadsDir)

    const filePath = path.join(uploadsDir, `dataFile_${uploadCount++}`)
    const stream = fs.createWriteStream(filePath)

    // file download
    stream.on('open', () => {
        req.pipe(stream)
    })

    stream.on('error', err => {
        console.log(err)
        res.status(500).json({ message: err, status: 'error' })
    })

    // when download completed, process data
    stream.on('close', () => {
        processData(filePath)
                .then(msg => {

                    // delete downloaded file when done
                    fs.unlink(filePath, err => {
                        if (err) throw err
                        console.log(filePath, 'was deleted')
                    })
                    res.status(200).json({ message: msg })
                })
                .catch(err => {
                    console.error(err)
                    res.status(500).json({ message: err })
                })
    } )
}

// process given file line by line
const processData = (file) => {
    return new Promise ((resolve, reject) => {

        const re = {
            // code ; dep ; id ; first ; last ; visit_time ; email
            TYPE1: /.{43}=;[A-Z]+(-[0-9]+)?;(\d{10,}|\d,\d+E\+\d+);[A-Z][a-z]+;[A-Z][a-z]+;\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2};[a-z]+@[a-z]+\.[a-z]+/,
            // code ; dep ; visit_time ; first ; last ; email ; id
            TYPE2: /.{43}=;[A-Z]+(-[0-9]+)?;\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2};[A-Z][a-z]+;[A-Z][a-z]+;[a-z]+@[a-z]+\.[a-z]+;(\d{10,}|\d,\d+E\+\d+)/
        }

        let lr

        try {
            lr = readline.createInterface({
                input: fs.createReadStream(file)
            })
        } catch (err) {
            reject(err)
        }
        
        let bulk = []
        let type = null


        // process line by line
        lr.on('line', async line => {

            // check what format the contents are in
            if (!type) {
                if (line.match(re.TYPE1))
                    type = 1
                else if (line.match(re.TYPE2))
                    type = 2
                else if (line.match(/^[\w;_]$/))
                    type = 3
            }

            if (type !== null) {
                
                let [ code, department, idCode, firstName, lastName, visitTime, email ] = [ null, null, null, null, null, null, null ]

                if (type === 1) {
                    [ code, department, idCode, firstName, lastName, visitTime, email  ] = line.trim().split(';')
                }
                else if (type === 2) {
                    [ code, department, visitTime, firstName, lastName, email, idCode  ] = line.trim().split(';')
                }
                else if (type === 3) {
                    [ code, department, visitTime, firstName, lastName, email, idCode  ] = retrieveValues(line)
                }

                // check if all data is available
                if ( code && department && visitTime && firstName && lastName && email && idCode ) {

                    // convert scientific format to normal numbers
                    if (idCode.match(/E\+/))
                        idCode = idCode.replace(/,/, '.').replace(/E\+/, 'E')
                        idCode = Number(`${idCode}`).toString() // convert scientific to normal

                    // get additional info from id code and visitTime
                    const [birthDate, gender, age] = getFromId(idCode, visitTime)
                    bulk.push({
                        code,
                        department,
                        visitTime,
                        firstName,
                        lastName,
                        email,
                        idCode,
                        birthDate,
                        gender,
                        age
                    })

                    // bulk add data to database when array reaches 50000 entries
                    if (bulk.length === 50000) 
                        Data.bulkCreate(bulk, { ignoreDuplicates: false })
                                .catch(err => {
                                    reject(err)
                                })
                                .finally(() => bulk = [])

                }
            }

        })

        lr.on('close', async () => {

            // bulk add left over data from array
            Data.bulkCreate(bulk, { ignoreDuplicates: false })
                    .catch(err => {
                        reject(err)
                    })
                    .finally(() => bulk = [])
            
            resolve()
        })
    })
}

// if no knows file data format was found then this should get the info anyways
const retrieveValues = (text) => {

    // grouped regex matching the data in a line
    const re = {
        CODE: /(?<code>.{43}=)/,
        DEP: /(?<department>[A-Z]+(-[0-9]+)?)/,
        TIME: /(?<time>\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2})/,
        NAME: /(?<first>[A-Z][a-z]+);(?<last>[A-Z][a-z]+)/,
        EMAIL: /(?<email>[a-z]+@[a-z]+\.[a-z]+)/,
        ID: /(?<idCode>(\d{10,}|\d,\d+E\+\d+))/
    }

    let code, dep, time, first, last, email, id, out

    // find the data in a line
    try {
        code = text.match(re.CODE).groups.code
        out = text.replace(re.CODE, '')
        
        email = out.match(re.EMAIL).groups.email
        out = out.replace(re.EMAIL, '')
        
        time = out.match(re.TIME).groups.time
        out = out.replace(re.TIME, '')
        
        id = out.match(re.ID).groups.idCode
        out = out.replace(re.ID, '')
        
        const matches = out.match(re.NAME).groups
        out = out.replace(re.NAME, '')
        first = matches.first
        last = matches.last
        
        dep = out.match(re.DEP).groups.department
        out = out.replace(re.DEP, '')
    } catch (err) {
        return [ null, null, null, null, null, null, null ]
    }

    return [ code, dep, time, first, last, email, id ]
}

// read additional info from id code and convert into readable format
const getFromId = (id, visitTime) => {
    const gender = (id.match(/^(1|3|5|7).*$/)) ? "M" : "F"
    let year, month, day, date
    switch (id[0]) {
        case '1':
        case '2':
            year = '18'
            break;
        case '3':
        case '4':
            year = '19'
            break;
        case '5':
        case '6':
            year = '20'
            break;
        case '7':
        case '8':
            year = '21'
            break;
    
        default:
            break;
    }

    year += id.substring(1, 3) // birth year
    month = id.substring(3, 5) // birth month
    day = id.substring(5, 7) // birth day
    date = Date.UTC(Number(year), Number(month), Number(day)) //birth date
    let visitDate = visitTime.split(' ')[0].split('-') // visit date string
    visitTimeDate = Date.UTC(Number(visitDate[0]), Number(visitDate[1]), Number(visitDate[2])) // visit date
    ageDate = new Date(visitTimeDate - date)
    age = String(Math.abs(ageDate.getUTCFullYear() - 1970)) // full age at visit time

    return [ `${month}/${day}/${year}`, gender, age ]
}

module.exports = {upload, getByIdCode}