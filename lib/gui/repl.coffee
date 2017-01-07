blessed = require 'blessed'
_       = require 'lodash'

Repl = (screen) ->
  term = blessed.terminal({
    parent: screen,
    cursor: 'line',
    cursorBlink: true,
    screenKeys: false,
    label: ' repl ',
    left: 0,
    bottom: 0,
    width: '100%',
    height: '20%',
    border: 'line',
    style: {
      fg: 'default',
      bg: 'default',
      focus: {
        border: {
          fg: 'green'
        }
      }
    }
  })

  term.pty.on 'data', (data) ->
    screen.log(JSON.stringify(data))

  term

module.exports = Repl
