blessed = require 'blessed'
_       = require 'lodash'

Screen = (client) ->
  screen = blessed.screen
    smartCSR: true
    input: client
    output: client
    terminal: 'xterm-256color'
    fullUnicode: true

  screen.title = "DND"

  box = blessed.box {
    top: 'center'
    left: 'center'
    width: '50%'
    height: '50%'
    content: 'Hello {bold}WORLD{/bold}'
    tags: true
    border:
      type: 'line'
    style:
      fg: 'white'
      bg: 'magenta'
      border:
        fg: '#f0f0f0'
      hover:
        bg: 'green'
  }

  screen.append box

  assets = __dirname + '../assets/'

  testTube = assets + 'icon_science.png'
  troll    = assets + 'internet-troll.jpg'
  werewolf = assets + 'werewolf.png'
  map      = assets + 'worldgenerator/Cyriev_elevation.png'

  console.log map

  file = map
  icon = blessed.image
    parent: screen
    top: 0
    left: 0
    type: 'ansi'
    file: file
    search: false

  console.log icon.scale

  box.on 'click', (data) ->
    box.setContent('{center}Some different {red-fg}content{/red-fg}.{/center}')
    screen.render()

  box.key 'enter', (ch, key) ->
    box.setContent('{right}Even different {black-fg}content{/black-fg}.{/right}\n')
    box.setLine(1, 'bar')
    box.insertLine(1, 'foo')
    screen.render()

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
    #icon.width = icon.height = "100%"
    #icon.scale = mapScale
    icon.setImage(map)

  screen.key ['r'], (ch, key) ->
    #icon.width = icon.height = '100%'
    #icon.scale = 1
    #icon.parent = box
    icon.setImage(if icon.file is werewolf then troll else werewolf)

  box.focus()

  setInterval ->
      #box.style.bg =
        #if box.style.bg is 'green' then 'magenta' else 'green'

      screen.render()
  ,100

  screen.data.main = box

  return screen

module.exports = Screen
