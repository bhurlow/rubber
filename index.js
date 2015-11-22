'use strict';

var suspend = require('suspend')
var resume = suspend.resume
var path = require('path')
var r = require('rethinkdbdash')({ host: 'rethink' });
var request = require('superagent')
var util = require('./util')
var debuglib = require('debug')
var info = debuglib('info')
var debug = debuglib('debug')
const searchUrl = 'http://search:9200/'

function countTable(dbName, tableName) {
  return r.db(dbName)
  .table(tableName)
  .count()
  .run()
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
    let tableSize = yield countTable(dbName, tableName)
    let dataStream = r.db(dbName).table(tableName).toStream()

    dataStream
      .pipe(util.transform(function(chunk, done) {
        indexDoc('test', 'test', chunk, function(err, res) {
          debug('indexing:', dbName, tableName, fmtPercentage(count++, tableSize))
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
      info('indexing:', dbName + ':' + tableName, chunk.new_val.id)
      done()
    })
  }))
}

function ensureTable(dbName, tableName) {
  countTable(dbName, tableName)
    .error(function(err) {
      console.log(err.message)
      process.exit()
    })
}

function processTable(str) {
  let parts = str.split(':')
  // todo es6 destruct
  let dbName = parts[0]
  let tableName = parts[1]
  if (parts.length !== 2) throw new Error('must specify db and table like: \"db:table\"')
  console.log('starting work on db:', dbName, 'table:', tableName)

  ensureTable(dbName, tableName)
  watchTable(dbName, tableName)
  backfillTable(dbName, tableName, function() {
    info('finished backfill for', dbName + ':' + tableName)
  })
}

function usage() {
  console.log('node index.js <db-name>:<table-name>')
  console.log('node index.js <db-name>:<table-name> <db-name>:<table-name>')
  process.exit()
}

var args = process.argv.slice(2)
if (!args.length) return usage()

// start it up!
args.map(processTable)


