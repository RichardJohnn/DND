_ = require 'lodash'

class Battle
  constructor: (@entities, @sideIdentifier) ->
    @sides       = _.partial(@_sides, @entities, @sideIdentifier)
    @keys        = _.partial(@_keys, @sides)
    @paddedSides = _.partial(@_paddedSides, @sides, @keys, @repeatTilLength)
    @teamOrder   = _.partial(@_teamOrder, @paddedSides, @keys)
    @order       = _.partial(@_order, @teamOrder)

  _sides: (entities, sideIdentifier) ->
    _.groupBy entities, sideIdentifier

  _keys: (objWrapper) ->
    Object.keys(objWrapper())

  _order: (_teamOrder) ->
    _.flatten(
      _.spread(_.zip)(_teamOrder()))

  _paddedSides: (sides, keys, repeatTilLength) ->
    sides = sides()
    longestSide =
      _(sides)
      .map('length')
      .max()

    keys().map (key) ->
      sides[key] = repeatTilLength(sides[key], longestSide)

    sides

  repeatTilLength: (array, length) ->
    if array.length < length
      timesToConcat = length / array.length
      newArray = _.clone array
      _.times timesToConcat, ->
        newArray = newArray.concat(array)
      array = newArray

    if array.length > length
      array = array.slice(0, length)

    array


  _teamOrder: (paddedSides, keys) ->
    paddedSides = paddedSides()
    @_teamOrder = (_.shuffle(paddedSides[key]) for key in _.shuffle keys())

  length: -> @order.length

  pointer: 0

  currentActor: -> @order[pointer]

  step: () ->
    pointer =
      if @pointer is @length() - 1
        0
      else
        @pointer + 1

    @pointer = pointer

  run: () ->

module.exports = Battle

