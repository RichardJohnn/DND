
module.exports = (term, character) ->
  terminate = ->
    setTimeout (->
      term.grabInput false
      term.fullscreen false
      term.applicationKeypad false
      term.beep()
      setTimeout (->
        process.exit()
      ), 100
    ), 100


  term.on 'key', ( key , matches , data ) ->
    switch key
      #when '1', '2', '3', '4', '5', '6', '7', '8'
        #color = parseInt(key) - 1
      #when 'ALT_UP'
        #term.up 1
      #when 'ALT_DOWN'
        #term.down 1
      #when 'ALT_LEFT'
        #term.left 1
      #when 'ALT_RIGHT'
        #term.right 1
      #when 'CTRL_UP'
        #term.bgColor(color, ' ').left.up(1, 1).bgColor(color, ' ').left 1
      #when 'CTRL_DOWN'
        #term.bgColor(color, ' ').left.down(1, 1).bgColor(color, ' ').left 1
      #when 'CTRL_LEFT'
        #term.bgColor(color, ' ').left(2).bgColor(color, ' ').left 1
      #when 'CTRL_RIGHT'
        #term.bgColor(color, '  ').left 1
      #when 'SHIFT_UP'
        #term.bgColor(color + 8, ' ').left.up(1, 1).bgColor(color + 8, ' ').left 1
      #when 'SHIFT_DOWN'
        #term.bgColor(color + 8, ' ').left.down(1, 1).bgColor(color + 8, ' ').left 1
      #when 'SHIFT_LEFT'
        #term.bgColor(color + 8, ' ').left(2).bgColor(color + 8, ' ').left 1
      #when 'SHIFT_RIGHT'
        #term.bgColor(color + 8, '  ').left 1
      when 'UP'
        character.up()
      when 'DOWN'
        character.down()
      when 'LEFT'
        character.left()
      when 'RIGHT'
        character.right()
      when 'ENTER'
        character.shit()
      when 'g'
        character.pickup()
      when 'd'
        character.drop()
      when 'x'
        character.die()
      when 'TAB'
        character.drink()
      when 'CTRL_C'
        terminate()

