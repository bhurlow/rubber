
var net = require('net')

var conn = net.connect(5001, 'localhost')

conn.on('connect', function(sock) {

  // conn.write('ready')
  // conn.write(' (require \'rubber.core ) ')
  // conn.write('1 + 1')
  conn.write(' var globacg = 700000 ')
  conn.write("\0")
})


conn.on('data', function(d) {
  console.log(d.toString())
})




