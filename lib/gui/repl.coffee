
blessed = require 'blessed'
_       = require 'lodash'

Repl = (screen) ->
  blessed.terminal({
    parent: screen,
    cursor: 'line',
    cursorBlink: true,
    screenKeys: false,
    label: ' multiplex.js ',
    left: 0,
    top: 0,
    width: '50%',
    height: '50%',
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

module.exports = Repl
