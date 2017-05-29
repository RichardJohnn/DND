expect     = require('chai').expect
ShowScreen = require '../../lib/gui/show-screen'
RandomLevel = require '../../lib/level/random-level'
#_         = require 'lodash'

describe 'ShowScreen', ->
  beforeEach ->
  it 'displays', ->
    randomLevel = new RandomLevel({sizeX: 10, sizeY: 11})
    ShowScreen(randomLevel.blocks)


