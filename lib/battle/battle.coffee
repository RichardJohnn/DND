_ = require 'lodash'

class Battle
  constructor: (@entities, @sideIdentifier) ->
    @sides      = _.groupBy @entities, @sideIdentifier
    @_keys      = Object.keys(@sides)
    @_teamOrder = (_.shuffle(@sides[key]) for key in _.shuffle @_keys)
    @order      =
      _.flatten(
        _.spread(_.zip)(@_teamOrder))


  step: () ->

  run: () ->

module.exports = Battle

