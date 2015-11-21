'use strict';

const searchUrl = 'http://search:9200/'
var suspend = require('suspend')
var request = require('superagent')
var resume = suspend.resume
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

function dropTable(cb) {
  r.db('test')
   .table('test')
   .delete()
   .run()
   .then(function(res) {
     cb(null, 'dropped')
   })
}

function fillTable(quant, cb) {
  let fakeData = Array(quant).fill().map((x) => (makeFakeUser())) 
  let fakeStream = es.readArray(fakeData)
  let tableStream = r.db('test').table('test').toStream({writable: true})
  fakeStream
    .pipe(tableStream)
    .on('finish', function() {
      cb(null, 'db filled ;)')
    })
    .on('error', cb)
}

function deleteIndex(dbName, cb) {
  let url = searchUrl +  dbName
  request('DELETE', searchUrl + dbName)
   .end(function(err, res) {
     cb(err, res)
   })
}

// prep testing/coding env
function bootstrap(quant) {
  suspend(function*() {

    console.log('deleting search index index')
    let res = null;

    try { res = yield deleteIndex('test', resume()) }
    catch (e) {
      console.log('search endpoint error')
    }

    console.log('dropping table')
    let drop = yield dropTable(resume())
    console.log(drop)

    console.log('filling db')
    let db = yield fillTable(quant, resume())
    console.log(db)
    process.exit()
  
  })()
}

bootstrap(100)
