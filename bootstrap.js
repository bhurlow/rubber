
var es = require('event-stream')
var r = require('rethinkdbdash')({ host: 'rethink' });
var faker = require('faker');

function makeFakeUser() {
  return {
    name: faker.name.findName(),
    email: faker.internet.email(),
    title: faker.name.jobTitle(),
    state: faker.address.state()
  }
}

function insertUser(user, cb) {
  r.db('test')
   .table('test')
   .insert(user)
   .run()
   .then(cb)
}

var thru = es.through(function(data) {
  console.log('working on', data.email)
  var self = this
  insertUser(data, function(res) {
    self.emit('data', res.generated_keys)
  })
})

function fillTable() {

  let fakeData = Array(100000).fill().map( (x) => (makeFakeUser())) 
  let dataStream = es.readArray(fakeData)

  dataStream
    .pipe(thru)
    .pipe(es.stringify())
    .pipe(process.stdout)
}

