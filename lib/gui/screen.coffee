blessed = require 'blessed'
_       = require 'lodash'
#Repl    = require './repl'
Map     = require './map'

Screen = (client) ->
  screen = blessed.screen
    title: 'DND'
    smartCSR: true
    input: client
    output: client
    terminal: 'xterm-256color'
    fullUnicode: true

  map   = Map(screen)
  image = map.image
  show  = map.show
  box   = map.box

  screen.append map.box

  #repl = Repl(screen)
  #screen.append repl

  screen.key ['escape', 'q', 'C-c'], (ch, key) ->
    screen.destroy()
    unless client?
      process.exit(0)

  mapStepSizeX  = 50
  mapStepSizeY = 20
  walk = (obj, prop, val) ->
    obj[prop] += val
    screen.render()
    #file = if Math.random() > .5 then '' else '2'
    #player.play("./lib/assets/sound/steps#{file}.wav", 5)
  moveImage = _.partial(walk, image)

  #moveImage = (prop, val) ->
    #show(prop, val)
    #screen.render()

  screen.key ['j'], (ch, key) -> moveImage('top',  -1 * mapStepSizeY)
  screen.key ['k'], (ch, key) -> moveImage('top',  mapStepSizeY)
  screen.key ['l'], (ch, key) -> moveImage('left', -1 * mapStepSizeX)
  screen.key ['h'], (ch, key) -> moveImage('left', mapStepSizeX)

  mapScale = 1

  #screen.key ['m'], (ch, key) ->
    #map.show()

  #screen.key ['r'], (ch, key) ->
    #map.hide()

  box.focus()

  return screen

module.exports = Screen

