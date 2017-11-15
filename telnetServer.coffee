Screen  = require './lib/gui/screen'
telnet = require('telnet2')

Server = ->
  server = telnet {tty:true}, (client) ->
    screen = Screen(client)

    screen.key ['escape', 'q', 'C-c'], (ch, key) -> screen.destroy()

    screen.on 'destroy', () ->
      if (client.writable)
        client.destroy()

    client.on 'debug', (msg) ->
      console.error(msg)

    client.on 'term', (terminal) ->
      screen.terminal = terminal
      screen.render()

    client.on 'size', (width, height) ->
      client.columns = width
      client.rows = height
      client.emit('resize')

    client.on 'close', () ->
      unless screen.destroyed
        screen.destroy()

  server.listen(2300)
  console.log('Listening on 2300...')

module.exports = Server
