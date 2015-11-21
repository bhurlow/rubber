'use strict';

var suspend = require('suspend')
var resume = suspend.resume
var path = require('path')
var r = require('rethinkdbdash')({ host: 'rethink' });
var request = require('superagent')
var util = require('./util')
const searchUrl = 'http://search:9200/'

function countTable(table, cb) {
  r.db('test')
   .table(table)
   .count()
   .run()
   .then(function(doc) {
     return cb(null, doc)
   })
}

function indexDoc(dbName, tableName, obj, cb) {
  let url = searchUrl + path.join(dbName, tableName, obj.id)
  request
    .put(url)
    .set('Accept', 'application/json')
    .send(obj)
    .end(cb)
}

function fmtPercentage(count, total) {
  return '% ' + Math.floor(count / total * 100)
}

function backfillTable(dbName, tableName, cb) {
  suspend(function*() {

    let count = 0
    let tableSize = yield countTable(tableName, resume())
    let dataStream = r.db(dbName).table(tableName).toStream()

    dataStream
      .pipe(util.transform(function(chunk, done) {
        indexDoc('test', 'test', chunk, function(err, res) {
          console.log('indexing:', dbName, tableName, fmtPercentage(count++, tableSize))
          done()
        })
      }))
      .on('finish', function() {
        console.log('finished!')
      })
      .on('end', function() {
        console.log('END EVENT')
      })
    })();
}

backfillTable('test', 'test', function() {
  console.log('BACKFILL DONE!')
})

// userStream

// r.db('test')
//  .table('test')
//  .get('0003a19e-0bae-4061-a58e-142880dc15ca')
//  .run()
//  .then(function(res) {
//    indexDoc('test', 'test', res, function(err, res) {
//      console.log(err)
//      console.log(res.body)
//    })
//  })

// function backFillTable(tableName,   ) { }


// userStream.pipe(es.stringify()).pipe(process.stdout)
// var trans = new Transformer()

// userStream.on('end', function(d) {
//   console.log('streaming over!')
// })

// userStream
//   .pipe(trans)
//   .pipe(process.stdout)
//   .on('finish', function() {
//     console.log('done writing!')
//   })

// userStream.on('data', function(d) {
//   console.log(d)
// })




