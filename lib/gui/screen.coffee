blessed = require 'blessed'
_       = require 'lodash'
Map     = require './map'

Screen = (client) ->
  screen = blessed.screen
    title: 'DND'
    smartCSR: true
    input: client
    output: client
    terminal: 'xterm-256color'
    fullUnicode: true

  map = Map(screen)
  icon = map.icon
  box = map.box

  screen.append map.box

  screen.key ['escape', 'q', 'C-c'], (ch, key) ->
    screen.destroy()
    unless client?
      process.exit(0)

  mapStepSize = 10
  walk = (wat, huh, where) ->
    wat[huh] += where
    #file = if Math.random() > .5 then '' else '2'
    #player.play("./lib/assets/sound/steps#{file}.wav", 5)
  walkDatIcon = _.partial(walk, icon)

  screen.key ['j'], (ch, key) -> walkDatIcon('top',  -1 * mapStepSize)
  screen.key ['k'], (ch, key) -> walkDatIcon('top',  mapStepSize)
  screen.key ['l'], (ch, key) -> walkDatIcon('left', -1 * mapStepSize)
  screen.key ['h'], (ch, key) -> walkDatIcon('left', mapStepSize)

  mapScale = 1

  screen.key ['m'], (ch, key) ->
    map.show()

  screen.key ['r'], (ch, key) ->
    map.hide()

  box.focus()

  setInterval ->
    screen.render()
  ,100

  return screen

module.exports = Screen

