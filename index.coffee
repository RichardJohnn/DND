arg = process.argv[2]

if arg is 'server'
  require('./telnetServer.coffee')()
else
  screen = require('./screen.coffee')()

