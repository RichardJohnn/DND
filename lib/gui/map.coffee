blessed = require 'blessed'
join    = require('path').join

Map = (screen) ->
  box = blessed.box {
    top: 'center'
    left: 'center'
    width: '50%'
    height: '50%'
    content: 'Hello {bold}WORLD{/bold}'
    tags: true
    style:
      fg: 'white'
      bg: 'magenta'
      border:
        fg: '#f0f0f0'
      hover:
        bg: 'green'
  }

  box.on 'click', (data) ->
    box.setContent("""
      {center}
        Some different {red-fg}content{/red-fg}.
      {/center}""")
    screen.render()

  box.key 'enter', (ch, key) ->
    box.setContent("""
    {right}
      Even different {black-fg}content{/black-fg}.
    {/right}\n""")
    box.setLine(1, 'bar')
    box.insertLine(1, 'foo')
    screen.render()

  map = join(__dirname, '../assets/worldgenerator/Cyriev_elevation.png')
  #map = join(__dirname, '~/1_1_3.tga')

  image = blessed.image
    parent: box
    top: 0
    left: 0
    type: 'ansi'
    file: map
    search: false

  {
    show: (prop, val) ->
      image.setImage(map)
    hide: ->
      image.clearImage()

    box: box
    image: image
  }

module.exports = Map
