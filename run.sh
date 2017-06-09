#!/usr/local/bin/coffee
ShowScreen = require './lib/gui/show-screen'
RandomLevel = require './lib/level/random-level'
randomLevel = new RandomLevel({sizeX: 10, sizeY: 11})
ShowScreen(randomLevel.blocks)



