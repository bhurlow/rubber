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

function changesFeed(dbName, tableName) {
  return r.db(dbName)
  .table(tableName)
  .changes()
  .toStream()
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
      .on('finish', cb)
    })();
}

function watchTable(dbName, tableName) {
  let dataStream = changesFeed(dbName, tableName)
  dataStream.pipe(util.transform(function(chunk, done) {
    indexDoc(dbName, tableName, chunk.new_val, function(err, res) {
      console.log('indexing:', dbName + ':' + tableName, chunk.new_val.id)
      done()
    })
  }))
}

// watchTable('test', 'test')

// backfillTable('test', 'test', function() {
//   console.log('finished backfill for test:test')
// })

