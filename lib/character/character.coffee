_ = require 'lodash'

class Character
  name: ''
  position: null
  inventory: []

  strength:      0
  dexterity:     0
  intelligence:  0
  constitution:  0
  widsom:        0
  charisma:      0

  body: null

  hp: 0

  dead: -> @hp <= 0

  act: ->
    action = {}

    if _.some(@foes)
      if 'attack' in @actions
        action.target = _.sample(@foes)
        action.action = 'attack'

    action


module.exports = Character
