_ = require 'lodash'

class Team
  constructor: (@entities) ->
    @body = @entities

  dead: ->
    _.every @body, 'dead'

module.exports = Team
