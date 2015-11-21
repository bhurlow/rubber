
'use strict';

var r = require('rethinkdbdash')({ host: 'rethink' });
var request = require('superagent')

function countTable(table, cb) {
  r.db('test')
   .table(table)
   .count()
   .run()
   .then(cb)
}

function indexDoc(obj) {
  request
    .put('http://search:9200')
}

var stream = require('stream');

class Transformer extends stream.Transform {

  constructor(options) {
    super({
      readableObjectMode : true,
      writableObjectMode: true
    });
  }

  _transform(chunk, encoding, done) {
    this.push(chunk.email)
    done()
  }

}

var es = require('event-stream')
var fs = require('fs')
var file = fs.createWriteStream('file.txt');
var userStream = r.db('test').table('test').toStream();

// userStream.pipe(es.stringify()).pipe(process.stdout)
var trans = new Transformer()

// userStream.on('end', function(d) {
//   console.log('streaming over!')
// })

userStream
  .pipe(trans)
  .pipe(process.stdout)
  .on('finish', function() {
    console.log('done writing!')
  })

// userStream.on('data', function(d) {
//   console.log(d)
// })




