blessed = require 'blessed'

screen = blessed.screen({fastCSR: true})

screen.title = "DND"

box = blessed.box
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

screen.append box

testTube = __dirname + '/lib/assets/icon_science.png'
troll    = __dirname + '/lib/assets/internet-troll.jpg'
werewolf = __dirname + '/lib/assets/werewolf.png'
map      = __dirname + '/lib/assets/elevation.png'
bigmap   = __dirname + '/lib/assets/worldgenerator/seed_33484_elevation.png'

file = bigmap
icon = blessed.image
  parent: screen
  top: 0
  left: 0
  type: 'ansi'
  width: '100%'
  #height: '100%'
  scale: .1
  file: file
  search: false

box.on 'click', (data) ->
  box.setContent('{center}Some different {red-fg}content{/red-fg}.{/center}')
  screen.render()

box.key 'enter', (ch, key) ->
  box.setContent('{right}Even different {black-fg}content{/black-fg}.{/right}\n')
  box.setLine(1, 'bar')
  box.insertLine(1, 'foo')
  screen.render()

screen.key ['escape', 'q', 'C-c'], (ch, key) -> process.exit(0)

screen.key ['j'], (ch, key) -> icon.top += 10
screen.key ['k'], (ch, key) -> icon.top -= 10

mapScale = .1

screen.key ['m'], (ch, key) ->
  icon.width = icon.height = "100%"
  #icon.scale = mapScale
  icon.parent = screen
  icon.setImage(bigmap)

screen.key ['r'], (ch, key) ->
  icon.width = icon.height = '10%'
  icon.scale = 1
  icon.parent = box
  icon.setImage(if icon.file is werewolf then troll else werewolf)

box.focus()

setInterval ->
  box.style.bg =
    if box.style.bg is 'green' then 'magenta' else 'green'

  icon.scale = mapScale
  #icon.setImage(
    #if icon.file is werewolf then troll else werewolf)
  screen.render()
,100

screen.render()

